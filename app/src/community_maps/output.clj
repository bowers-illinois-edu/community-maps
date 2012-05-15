(ns community-maps.output
  (:use shanks.appengine-magic
        hiccup.page-helpers)
  (:import com.google.appengine.api.datastore.Entity
           com.google.appengine.api.datastore.PreparedQuery
           com.google.appengine.api.datastore.Query
           com.google.appengine.api.datastore.Text
           com.google.appengine.api.datastore.FetchOptions
           com.google.appengine.api.datastore.FetchOptions$Builder
           com.google.appengine.api.datastore.QueryResultList
           com.google.appengine.api.datastore.Cursor
           shanks.appengine_magic.Subject
           java.util.Date
           java.text.SimpleDateFormat
           ; Blob service file like storage stuff
           com.google.appengine.api.files.FileService
           com.google.appengine.api.files.AppEngineFile
           com.google.appengine.api.files.FileWriteChannel
           com.google.appengine.api.files.FileServiceFactory
           java.nio.ByteBuffer
           java.io.PrintWriter
           java.nio.channels.Channels)
  (:require
   [appengine-magic.services.blobstore :as bs]
   [appengine-magic.services.datastore :as ds]
            [appengine-magic.services.task-queues :as tq]))


(def date-formatter (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss Z"))

(defn fetch-comments
  [n]
  "Get comments for the n-th step (1-8)"
  (ds/query :kind shanks.appengine_magic.Subject
            :filter (>  (keyword (str "comments-" n)) "")))

(defn comments-page
  [_]
  (let [steps (range 1 9)
        all-comments (zipmap steps (map fetch-comments steps))]
    (xhtml
     [:head [:title "Comments Display Page"]]
     [:body
      [:table
       [:th "Step"] [:th "Comments"] [:th "Email"]
       (map
        (fn [[step comments]]
          (map #(vector :tr [:td step] [:td (get % (keyword (str "comments-" step)))]
                        [:td (get % :email-address)])
               comments))
        all-comments)]])))

(defn escape-str-for-csv
  "Escape all quotes and wrap in a set of quotes"
  [s]
  (str "\""
       (reduce
        (fn [i [k v]] 
          (.replaceAll (re-matcher k i) v))
        s
        {#"\\" "\\\\\\\\" ; this turns a single slash into two,
                          ; really, I swear.
         #"\"" "\\\\\"" })
       "\""))

(defn exportable-subject
  "Turn dates into a readable format, escapes strings"
  [subject]
  (let [subject (into {} (.getProperties subject))
        ks (keys subject)
        text-keys (filter #(= Text (class (get subject %))) ks)
        string-keys (filter #(string? (get subject %)) ks)
        date-keys (filter #(= Date (class (get subject %))) ks)]
    (-> subject
        (into (map #(vector % (escape-str-for-csv (.getValue (get subject %)))) text-keys))
        (into (map #(vector % (str "\"" (.format date-formatter (get subject %)) "\"")) date-keys))
        (into (map #(vector % (escape-str-for-csv (get subject %))) string-keys)))))

(defn subject->yaml
  "Takes a subject object and turns it into a YAML string representation"
  [subject]
  (let [exportable (exportable-subject subject)]
    (apply
     str
     "- id: " (.getId (.getKey subject)) "\n"
     (doall (interpose "\n" (map (fn [[k v]] (str "  " k ": " v)) exportable))))))

(ds/defentity DataYAML [timestamp blobkey])

(def *subjects-per-task* 200)

(defn data-dump-subjects
  "Given an optional cursor string, generate and fetch a query from the datastore"
  [cursor]
  (let [datastore (ds/get-datastore-service)
        pq (.prepare datastore (Query. "Subject"))
        fo (FetchOptions$Builder/withLimit *subjects-per-task*)]
    (when cursor (.startCursor fo (Cursor/fromWebSafeString cursor)))
    (.asQueryResultList pq fo)))

;;; Using the blobstore service to hold large CSV files and then
;;; streaming directly to users

(defmacro with-open-blob 
  "Open a blob for writing and bind it to *out* using a PrintWriter object"
  [path & body]
  `(let [fs# (FileServiceFactory/getFileService)
         file# (AppEngineFile. ~path)
         channel# (.openWriteChannel fs# file# false)]
     (with-open [pw# (PrintWriter. (Channels/newWriter channel# "UTF8"))]
       (binding [*out* pw#]
         ~@body))))

(defn create-blob-file
  "Start a blobstore file of the some text at the top" 
  [text]
  (let [fs (FileServiceFactory/getFileService)
        file (.createNewBlobFile fs "application/octet-stream")
        wc (.openWriteChannel fs file false)]
    (doto wc
      (.write
       (ByteBuffer/wrap
        (.getBytes (str text "\n"))))
      (.close))
    (.getFullPath file)))

(defn finalize-blobfile
  "Finalize a path so that it can be read but not written to, returns blob key"
  [path]
  (let [fs (FileServiceFactory/getFileService)
        file (AppEngineFile. path)
        channel (.openWriteChannel fs file true)]
    (.closeFinally channel)
    (.getBlobKey fs file)))

(defn build-data-cron
  "Kick off the task to build; cron.xml will hit this regularly"
  [_]
  (let [path (create-blob-file (str "# Data dump started on " (.format date-formatter (Date.))))]
    (tq/add! :url "/data/dump-data" :method :get :params {:filepath path})
    {:status 200 :headers {"Content-Type" "text/plain"} :body (str "Data YAML building queued. File path: " path)}))

(defn build-data-dump
  "The the cron job kicks off another URL to actually do the work."
  [req]
  (let [filepath (get-in req [:params "filepath"])
        cursor (get-in req [:params "cursor"])
        data (data-dump-subjects cursor)]
    (if (= 0 (count data))
      (ds/save! (DataYAML. (Date.) (finalize-blobfile filepath)))
      (do
        (with-open-blob filepath
          (doseq [i data]
            (println (subject->yaml i))))
        (tq/add! :url "/data/dump-data" :method :get :params {:filepath filepath :cursor (.toWebSafeString (.getCursor data))}))))
  {:status 200 :headers {"Content-Type" "text/plain"} :body "YAML at /data/data.yaml"})

(defn data-yaml-file
  [req]
  (bs/serve req
            (:blobkey
             (first
              (ds/query :kind DataYAML :limit 1 :sort [[:timestamp :dsc]])))))



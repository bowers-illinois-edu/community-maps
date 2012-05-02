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


(def *sep* ",") ; separator for CSV files

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
       (.replaceAll (re-matcher #"\"" s) "\\\\\"")
       "\""))

(defn subject-csv-preprocess
  "Turn dates into a readable format, escapes strings"
  [subject]
  (let [ks (keys subject)
        text-keys (filter #(= Text (class (get subject %))) ks)
        string-keys (filter #(string? (get subject %)) ks)
        date-keys (filter #(= Date (class (get subject %))) ks)]
    (-> subject
        (into (map #(vector % (escape-str-for-csv (.getValue (get subject %)))) text-keys))
        (into (map #(vector % (str "\"" (.format date-formatter (get subject %)) "\"")) date-keys))
        (into (map #(vector % (escape-str-for-csv (get subject %))) string-keys)))))

(defn subject->csv
  "Turn a single subject into a csv string"
  ([headers subject]
     (apply str "\n" (interpose *sep* (map #(get subject %) headers)))))

(ds/defentity DataCSV [timestamp blobkey])

(def *subjects-per-task* 1)

(defn data-dump-subjects
  "Given an optional cursor string, generate and fetch a query from the datastore"
  [cursor]
  (let [datastore (ds/get-datastore-service)
        pq (.prepare datastore (Query. "Subject"))
        fo (FetchOptions$Builder/withLimit *subjects-per-task*)]
    (when cursor (.startCursor fo (Cursor/fromWebSafeString cursor)))
    (.asQueryResultList pq fo)))

(defn build-data-cron
  "Kick off the task to build; cron.xml will hit this regularly"
  [_]
  (tq/add! :url "/data/build-csv" :method :get)
  {:status 200 :headers {"Content-Type" "text/plain"} :body "Data CSV building queued."})

(defn all-subject-csv-string
  "Create the big string of all the data"
  []
  (subject->csv (ds/query :kind shanks.appengine_magic.Subject :filter (> :schema-version-number 1))))

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

(defn create-csv-blob
  "Start a blobstore file of the csv type, with a header given by the vector"
  [header]
  (let [fs (FileServiceFactory/getFileService)
        file (.createNewBlobFile fs "text/csv")
        wc (.openWriteChannel fs file false)]
    (doto wc
      (.write
       (ByteBuffer/wrap
        (.getBytes
         (apply str (doall (interpose *sep* header))))))
      (.write (ByteBuffer/wrap (.getBytes "\n")))
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

(defn build-data-csv
  "The the cron job kicks off another URL to actually do the work."
  [_]
  (let [csv (all-subject-csv-string)]
    ; (ds/save! (DataCSV. (Date.) (string->blob csv)))
    {:status 200 :headers {"Content-Type" "text/plain"} :body "CSV at /data/data.csv"}))

(defn all-data-csv
  [req]
  (bs/serve req
            (:blobkey
             (first
              (ds/query :kind DataCSV :limit 1 :sort [[:timestamp :dsc]])))))

;;; Make the CSV live

(defn live-csv 
  [_]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (all-subject-csv-string)})


(ns community-maps.output
  (:use shanks.appengine-magic
        hiccup.page-helpers)
  (:import com.google.appengine.api.datastore.Entity
           com.google.appengine.api.datastore.PreparedQuery
           com.google.appengine.api.datastore.Query
           com.google.appengine.api.datastore.Text
           shanks.appengine_magic.Subject
           java.util.Date
           java.text.SimpleDateFormat)
  (:require [appengine-magic.services.datastore :as ds]
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
  ([subjects] (subject->csv subjects "|"))
  ([subjects sep]
     (let [flat (map subject-csv-preprocess subjects)
           headers (sort (reduce (fn [a i] (into a (keys i))) #{} flat))]
       (str
        (apply str (interpose sep (map #(if (keyword? %) (subs (str %) 1) %) headers)))
        (apply str (map
                    (fn [subject]
                      (apply str "\n" (interpose sep (map #(get subject %) headers))))
                    flat))))))

(ds/defentity DataCSV [timestamp body])

(defn build-data-cron
  "Kick off the task to build; cron.xml will hit this regularly"
  [_]
  (tq/add! :url "/data/build-csv" :method :get)
  {:status 200 :headers {"Content-Type" "text/plain"} :body "Data CSV building queued."})

(defn all-subject-csv-string
  "Create the big string of all the data"
  []
  (subject->csv (ds/query :kind shanks.appengine_magic.Subject :filter (> :schema-version-number 1))))

(defn build-data-csv
  "The the cron job kicks off another URL to actually do the work."
  [_]
  ; schema-version-number is when the subjects were flattened
  ;
  ;(let [csv (all-subject-csv-string)]
  ;  (ds/save! (DataCSV. (Date.) (ds/as-text csv)))
    {:status 200 :headers {"Content-Type" "text/plain"} :body "CSV at /data/live-data.csv"})

(defn all-data-csv
  [_]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (.getValue
          (:body
           (first
            (ds/query :kind DataCSV :limit 1 :sort [[:timestamp :dsc]]))))})

;;; Make the CSV live

(defn live-csv 
  [_]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (all-subject-csv-string)})


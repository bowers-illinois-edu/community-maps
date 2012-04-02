(ns community-maps.output
  (:use shanks.appengine-magic
        hiccup.page-helpers)
  (:import com.google.appengine.api.datastore.Entity
           com.google.appengine.api.datastore.PreparedQuery
           com.google.appengine.api.datastore.Query
           shanks.appengine_magic.Subject)
  (:require [appengine-magic.services.datastore :as ds]))


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

(def split-pred (juxt filter remove))

(defn safe-name
  [input]
  (if (keyword? input) (subs (str input) 1) input))

(defn prefix-flatten
  "Squash a respondent into a flat map, where each key gets the parent as a prefix"
  ([coll] (prefix-flatten coll ""))
  ([coll prefix]
     (let [pfx (if (= prefix "") "" (str (safe-name prefix) "-"))
           ckeys (keys (dissoc coll :id :name-in-parent))
           [maps atoms] (split-pred #(map? (get coll %)) ckeys)
           flattened (map #(prefix-flatten (get coll %) %) maps)]
       (into {}
             (map
              (fn [[k v]] [(str pfx (safe-name k)) v])
                 (reduce
                  into 
                  (select-keys coll atoms)
                  flattened))))))


(defn subject->csv
  ([subjects] (subject->csv subjects "|"))
  ([subjects sep]
     (let [flat (map prefix-flatten subjects)
           headers (sort (reduce (fn [a i] (into a (keys i))) #{} flat))]
       (str
        (apply str (interpose sep headers))
        (apply str (map
                    (fn [subject]
                      (apply str "\n" (interpose sep (map #(get subject %) headers))))
                    flat))))))

(defn all-data-csv
  [_]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (subject->csv (dbload-all))})

;;; Some quick and dirty dumps to make tracking progress easier

(defn dbload-subjects
  "Load all subjects WITHOUT hierarchical data"
  []
  (ds/query :kind Subject))
    
(defn subjects-csv 
  [_]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (subject->csv (dbload-subjects))})

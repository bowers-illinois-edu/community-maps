(ns community-maps.output
  (:use shanks.appengine-magic
        hiccup.page-helpers)
  (:import com.google.appengine.api.datastore.Entity
           com.google.appengine.api.datastore.PreparedQuery
           com.google.appengine.api.datastore.Query)
  (:require [appengine-magic.services.datastore :as ds]))


(defn fetch-comments
  [n]
  "Get comments for the n-th step (1-8)"
  (->
   (.prepare (ds/get-datastore-service)
             (doto (Query. "Map")
               (.addFilter (str ":comments-" n)
                           com.google.appengine.api.datastore.Query$FilterOperator/NOT_EQUAL
                           "")))))

(defn comments-page
  [_]
  (let [steps (range 1 9)
        all-comments (zipmap steps (map fetch-comments steps))]
    (xhtml
     [:head [:title "Comments Display Page"]]
     [:body
      (map
       (fn [[step comments]]
         [:div.comments
          [:h2 "Page: " step]
          [:table
           [:th "Comments"] [:th "Email"]
           (map #(vector :tr [:td (.getProperty % (str ":comments-" step))]
                         [:td (.getProperty % ":email-address")])
                (.asIterable comments))]])
       all-comments)])))

(def split-pred (juxt filter remove))

(defn safe-name
  [input]
  (if (keyword? input) (subs (str input) 1) input))

(defn prefix-flatten
  "Squash a respondent into a flat map, where each key gets the parent as a prefix"
  ([coll] (prefix-flatten coll ""))
  ([coll prefix]
     (let [pfx (if (= prefix "") "" (str (safe-name prefix) "-"))
           ckeys (keys coll)
           [maps atoms] (split-pred #(map? (get coll %)) ckeys)
           flattened (map #(prefix-flatten (get coll %) %) maps)]
       (into {}
             (map
              (fn [[k v]] [(str pfx (safe-name k)) v])
                 (reduce
                  into 
                  (select-keys coll atoms)
                  flattened))))))

(defn get-all-subjects
  []
  (let [usersQuery (.asIterable
                    (.prepare
                     (ds/get-datastore-service)
                     (doto (Query. "Map")
                       (.addFilter ":step"
                                   com.google.appengine.api.datastore.Query$FilterOperator/GREATER_THAN
                                   0))))
        ids (map #(.getId (.getKey %)) usersQuery)]
    (map dbload ids)))

(defn csv
  [_]
  (let [sep " | "
        subjects (map prefix-flatten (get-all-subjects))
        headers (reduce (fn [a i] (into a (keys i))) #{} subjects)]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (str
            (apply str (interpose sep headers))
            (apply str (map
                        (fn [subject]
                          (apply str "\n" (interpose sep (map #(get subject %) headers))))
                        subjects)))}))

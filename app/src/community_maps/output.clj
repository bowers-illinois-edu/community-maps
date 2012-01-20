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


(ns community-maps.core
  (:use [shanks core appengine-magic]
        hiccup.page-helpers
        [burp.ring :only [wrap-burp]]
        [burp.jquery :only [jquery-link jquery-ui-link]])
  (:require [appengine-magic.core :as ae]))

(defn randomizer []
  (randomize-subject {:condition [true false]}))

(defn createwithid []
  (let [key (dbsave (randomizer))]
    (dbload (.getId key))))

(defn layout [body]
  (xhtml
   [:head
    [:title "Taking a survey"]
    (jquery-link) (jquery-ui-link)
    (include-js "burp.jquery.ui.support.js")]
   [:body body]))

(defscreen thank-you [_] "Thank you for taking this survey.")

(defscreen draw-on-map [subject] [:div "this is a test"])

(def survey-app
  (wrap-burp (survey createwithid dbsave dbload layout thank-you [draw-on-map])))

(ae/def-appengine-app survey-app-ae #'survey-app)





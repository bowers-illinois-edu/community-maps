(ns community-maps.core
  (:use [shanks core appengine-magic]
        hiccup.core
        hiccup.page-helpers
        [burp.ring :only [wrap-burp]]
        [burp.jquery :only [jquery-link jquery-ui-link]]
        ring.middleware.file
        [community-maps.screens address draw everything])
  (:require [appengine-magic.core :as ae]
            [hiccup.form-helpers :as f]))

(defn randomizer []
  (randomize-subject
   {:election-neighborhood ["national" "provincial" "like-live" "safety" "nothing"]
    :ethnic-work-up ["Chinese" "black"]
    :get-welfare ["ethnic minorities" "people"]
    :prefer-neighborhood ["ethnic minorities" "blacks" "Asians"]
    :leader-party ["Conservative" "NDP" "BQ" "Liberal"]
    :minority-projection ["25" "35" "50"]
    :minority-population-share ["increase" "decrease"]}))

(defn createwithid []
  (let [key (dbsave (randomizer))]
    (dbload (.getId key))))

(defn layout [body]
  (xhtml
   [:head
    [:title "Taking a survey"]
    (jquery-link) (jquery-ui-link)
    (include-js "burp.jquery.ui.support.js")
    (include-js "address.js")
    (include-js "map_main.js")
    (include-js "scribble.js")
    (include-js "utilities.js")
    (include-js "questions.js")
    (include-js "http://maps.google.com/maps/api/js?v=3.4&sensor=false")
    (include-css "http://yui.yahooapis.com/2.7.0/build/reset-fonts-grids/reset-fonts-grids.css")
    (include-css "http://yui.yahooapis.com/2.8.2r1/build/base/base-min.css")
    (include-css "https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/themes/ui-lightness/jquery-ui.css")
    (include-css "local.css")]
   [:body {:id "doc" :class "yui-t7"}
    [:div#hd [:h1 "Hello!"]]
    [:div#bd
     [:div.yui-g body]]]))

; screens defined in screens.* namespaces
(defscreen thank-you [_] "Thank you for taking this survey.")

(def survey-app
  (-> (survey createwithid dbsave dbload layout thank-you [address draw basics])
      wrap-burp))

(ae/def-appengine-app community-maps-app #'survey-app)



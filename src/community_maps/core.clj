(ns community-maps.core
  (:use [shanks core appengine-magic]
        hiccup.core
        hiccup.page-helpers
        [burp.core :only [add-class]]
        [burp.ring :only [wrap-burp]]
        [burp.jquery :only [jquery-link jquery-ui-link]]
        ring.middleware.file)
  (:require [appengine-magic.core :as ae]
            [hiccup.form-helpers :as f]))

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
    (include-js "burp.jquery.ui.support.js")
    (include-js "map_main.js")
    (include-js "scribble.js")
    (include-js "utilities.js")
    (include-js "http://maps.google.com/maps/api/js?v=3.4&sensor=false")
    (include-css "http://yui.yahooapis.com/2.7.0/build/reset-fonts-grids/reset-fonts-grids.css")
    (include-css "http://yui.yahooapis.com/2.8.2r1/build/base/base-min.css")
    (include-css "https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/themes/ui-lightness/jquery-ui.css")
    (include-css "local.css")]
   [:body {:id "doc" :class "yui-t7"}
    [:div#hd [:h1 "Hello!"]]
    [:div#bd
     [:div.yui-g body]]]))

(defscreen thank-you [_] "Thank you for taking this survey.")

(defelem button
  "Create a button with a txt label"
  [txt]
  [:a {:class "fg-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"} [:span.ui-button-text txt]])

(defelem scribble-map
  "Create a map centered on a given lat/lon"
  [id lat lon]
  (f/with-group id
    [:div.scribble-map
     (f/hidden-field {:class "lat"} :lat lat)
     (f/hidden-field {:class "lon"} :lon lon)
     (f/hidden-field {:class "map-data"} :data)
     [:div.actions
      (add-class (button "Start Drawing") "action start")
      (add-class
       (button {:style "display: none;"} "Stop Drawing")
       "action stop")
      (add-class
       (button {:style "display: none;"} "Reset Map")
       "action reset")]
     [:div.map-canvas {:style "height: 400px; width: 100%;"}]]))

(defscreen draw-on-map
  [subject]
  (question "Please draw on the map"
   (scribble-map :test-scribble 40.1105556 -88.2072222)))

(def survey-app
  (-> (survey createwithid dbsave dbload layout thank-you [draw-on-map])
      wrap-burp
      (wrap-file "js")
      (wrap-file "css")))

(ae/def-appengine-app survey-app-ae #'survey-app)





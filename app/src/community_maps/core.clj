(ns community-maps.core
  (:use [shanks core appengine-magic]
        hiccup.core
        hiccup.page-helpers
        [burp.ring :only [wrap-burp]]
        [burp.jquery :only [jquery-link jquery-ui-link]]
        ring.middleware.file
        [community-maps.screens address draw everything own-community minorities-community consent])
  (:require [appengine-magic.core :as ae]
            [hiccup.form-helpers :as f]
            [community-maps.gis :as gis]))

(defn randomizer []
  (randomize-subject
   {:election-neighborhood ["national" "provincial" "neighborhood" "nothing"]
    :ethnic-work-up ["Chinese" "blacks"]
    :get-welfare ["ethnic minorities" "people"]
    :prefer-neighborhood ["ethnic minorities" "black" "Asian"]
    :leader-party ["Conservative" "NDP" "BQ" "Liberal"]
    :minority-projection ["25" "35" "50"]
    :minority-population-share ["increase" "decrease"]
    :ethnic-shop ["ethnic minorities" "other members of their same ethnic background"]
    :outgroup-marry ["race" "ethnic background"]
    :display-district (keys gis/*districts*)}))

(defn createwithid []
  (let [key (dbsave (randomizer))]
    (dbload (.getId key))))

(defmulti layout (fn [subject screen] screen))

(def css (list
          (include-css "http://yui.yahooapis.com/2.7.0/build/reset-fonts-grids/reset-fonts-grids.css")
          (include-css "http://yui.yahooapis.com/2.8.2r1/build/base/base-min.css")
          (include-css "https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/themes/ui-lightness/jquery-ui.css")
          (include-css "local.css")))

(defn body [title body]
  [:body {:id "doc" :class "yui-t7"}
    [:div#hd [:h1 title]]
    [:div#bd
     [:div.yui-g body]]])
  
(defmethod layout :default [subject screen]
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
    css]
   (body (str "Survey Step " (:step subject 0)) (screen-form screen subject))))

;; other screens defined in screens.* namespaces
;; the thank you screen is special.
(defscreen thank-you [_] "Thank you for taking this survey.")
(defmethod layout thank-you [subject screen]
  (xhtml
   [:head
    [:title "Thank You"]
    css]
   (body "Thank You" (screen subject))))

;; Consent also gets a special layout function
(defmethod layout consent [subject screen]
  (xhtml
   [:head
    [:title "Welcome"]
    (jquery-link)
    (jquery-ui-link)
    (include-js "burp.jquery.ui.support.js")
    (include-js "questions.js")
    css]
   (body "Welcome" (screen-form screen subject))))

(def survey-app
  (-> (survey createwithid dbsave dbload layout 
              [consent
               address
               draw
               own-community
               randomized-district
               canada-population
               racial-ethnic
               racial-conflict
               minorities-community
               thank-you])
      wrap-burp))

(ae/def-appengine-app community-maps-app #'survey-app)



(ns community-maps.core
  (:use [shanks core appengine-magic]
        hiccup.core
        hiccup.page-helpers
        [burp.ring :only [wrap-burp]]
        [burp.jquery :only [jquery-link jquery-ui-link]]
        ring.middleware.file
        [community-maps.screens address draw everything own-community minorities-community]
        [clojure.string :only [split]])
  (:require [appengine-magic.core :as ae]
            [burp.forms :as bf]
            [hiccup.form-helpers :as f]
            [community-maps.gis :as gis]))

(defn randomizer []
  (randomize-subject
   {:feel-close-to ["control" "neighborhood" "city" "province" "Canada"]
    :get-welfare ["ethnic minorities" "people"]
    :minority-projection ["none" "25" "35" "50"]
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
  
(defn screen-form-button
  "Wrap a screen in a form with a funky button"
  [scrn subject]
  (f/form-to [:post "/"]
             (f/hidden-field :id (:id subject))
             (scrn subject)
             [:h4.comments-label (f/label (str "comments-" (:step subject)) "Questions or Comments? Click here.")]
             [:div.comments-subform
              [:p "If you have any questions or comments, please enter them in the box below. If you would like us to contact you about your question or comments, please include your email address below."]
              (f/text-area (str "comments-" (:step subject)))
              [:div.email (f/label :email-address "Email: ")
               (f/text-field :email-address (:email-address subject))]]
             (f/submit-button {:class "continue fg-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"} "Continue")))

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
    (include-js "resume.js")
    (include-js "http://maps.google.com/maps/api/js?v=3.4&sensor=false")
    css]
   (body "Mapping Communities Survey"
         (list
          [:a#resume {:class "fg-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"}
           [:span.ui-button-text "Resume Later"]]
          [:div#resume-popup
           [:p "You can pick up where you left off later. Just enter your email address, and we will send you a link to start from where you stopped."]
           [:input.email]]
          (screen-form-button screen subject)))))

;;; The consent and thank you pages have special layout functions,
;;; which makes writing them out a little more verbose (i.e. two
;;; functions), but it works best with the backend infrastructure this
;;; way

(defn load-and-process-txt
  "Load a text file and turn it into basic HTML. Returns a vector of [[:p \"paragraph 1\"] ...]"
  [filename]
  (map #(vector :p %)
       (split
        (slurp
         (ae/resource-url filename))
        #"\n\n")))

;; load this text at compile time to save processing
(def consent-text (load-and-process-txt "consent.txt"))
(defscreen consent
  [_]
  consent-text
  (bf/labeled-checkbox
   :consent
   (str
    "I understand the general nature of this survey, "
    "I am 18 years of age or older, and I voluntarily agree to participate in this survey.")))

(defmethod layout consent [subject screen]
  (xhtml
   [:head
    [:title "Welcome"]
    (jquery-link)
    (jquery-ui-link)
    (include-js "burp.jquery.ui.support.js")
    (include-js "questions.js")
    css]
   (body "Welcome" (screen-form-button screen subject))))

(def thank-text (load-and-process-txt "thank.txt"))

(defscreen thank-you [_] thank-text)

(defmethod layout thank-you [subject screen]
  (xhtml
   [:head
    [:title "Thank You"]
    css]
   (body "Thank You" (screen subject))))

;;; this is the app as called by the appengine-magic library
(def survey-app
  (-> (survey createwithid dbsave dbload layout 
              [consent
               address
               draw
               own-community
               randomized-district
               canada-population
               racial-ethnic
               minorities-community
               racial-conflict
               thank-you])
      wrap-burp))

(ae/def-appengine-app community-maps-app #'survey-app)



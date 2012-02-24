(ns community-maps.tags
  (:use [community-maps.util :only [map-vals]]
        [shanks core]
        [appengine-magic.core :only [appengine-environment-type]]
        hiccup.core
        [burp.core :only [add-class]]
        [burp.ring :only [wrap-burp]]
        [clojure.string :only [split join]]
        [community-maps.gis :only [from-quebec?]])
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]
            [burp.jquery :as bj]))

(defelem button
  "Create a button with a txt label"
  [txt]
  [:a {:class "fg-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"} [:span.ui-button-text txt]])

(def map-canvas [:div.map-canvas {:style "height: 400px; width: 100%;"}])

(defelem scribble-map
  "Create a map centered on a given lat/lon"
  [id lat lon zoom]
  (f/with-group id
    [:div.scribble-map
     (f/hidden-field {:class "lat"} :lat lat)
     (f/hidden-field {:class "lon"} :lon lon)
     (f/hidden-field {:class "zoom"} :zoom zoom)
     (f/hidden-field {:class "map-data"} :data)
     (f/hidden-field {:class "events"} :events)
     [:div.actions
      (add-class (button "Start Drawing") "action start")
      (add-class
       (button {:style "display: none;"} "Stop Drawing")
       "action stop")
      (add-class
       (button {:style "display: none;"} "Reset Map")
       "action reset")]
     map-canvas]))

(defelem agree-disagree
  "Strongly agree => strongly disagree"
  [id]
  (bf/radio-group id
                  {:strongly-agree "Strongly agree"
                   :agree "Agree"
                   :neither "Neither agree nor disagree"
                   :disagree "Disagree"
                   :strongly-disagree "Strongly disagree"}))

(defelem likelihood
  "Very likely => very unlikely"
  [id]
  (bf/radio-group id
                  {:very-likely "Very likely"
                   :likely "Likely"
                   :fifty-fity "Equally likely and unlikely (\"50/50\")"
                   :unlikely "Unlikely"
                   :very-unlikely "Very unlikely"}))


(def ethnic-groups
  {:black "Blacks"
   :white "Whites"
   :unemployed "Unemployed"
   :chinese "Chinese"
   :east-indian "East Indians"
   :aboriginal "Canadian Aboriginals"
   :latin "Latin Americans"
   :other-asian "Other Asians"})

(defn political-groups
  "The political groups, with BQ if the subject is in Quebec"
  [subject]
  (let [all-see
        {:liberal "Liberal Party"
         :conservative "Conservative Party"
         :ndp "New Democratic Party (NDP)"
         :green "Green Party"}]
    (if (from-quebec? subject)
      (assoc all-see :quebecois "Bloq Quebecois")
      all-see)))

;; take on "supporters" to the political groups
(defn ethnic-political-groups
  "Combines the ethnic and political groups, with BQ added for Quebec subjects"
  [subject]
  (merge
   ethnic-groups
   (map-vals #(str % " supporters") (political-groups subject))))

(defn group-sliders
  "Asks about the list of groups we are interested in"
  ([subject id prompt] (group-sliders subject id prompt "0%" "100%"))
  ([subject id prompt pre post]
     (let [grps (ethnic-political-groups subject)]
       (question
        prompt
        (f/with-group id
          [:table.groups
           (doall
            (map
             (fn [[group-id group]]
               [:tr
                [:td.group group]
                [:td pre]
                [:td {:width "60%"} (bj/slider group-id)]
                [:td post]])
             (conj
              (shuffle (vec (dissoc grps :other-asian)))
              [:other-asian (:other-asian grps)])))])))))

(defn learn-about-composition
  "How did the R learn about his community."
  [id prompt]
  (let [opts {:observation "personal observation"
              :friends "friends and families"
              :news "news (tv, radio, online, paper)"
              :institutions "local institutions"
              :leaders "political leaders"
              :tv "television entertainment shows"}]
    (f/with-group id
      [:div.learn-composition
       [:div.mc
        (multiple-choice :learn (str prompt " Please check all that apply.") opts)]
       [:div.sc
        (single-choice :important "Of these, which was most important?" opts)]])))

(defn seven-point-scale
  "Rate from 1 to 7"
  [id low high prompt]
  (question prompt
            [:span low] (bj/slider id) [:span high]))
                                        ;(add-class
                                        ; (question
                                        ;  prompt
                                        ;  [:div.instrument
                                        ;   [:span.low low]
                                        ;   (bf/radio-group id (map #(vector % %) (range 1 8)))
                                        ;   [:span.high high]
                                        ;   [:br]])
                                        ; "seven-point-scale"))

(defn directions
  "Provide a set directions inline with the questions"
  [& body]
  [:div.directions (doall (map #(vector :p %) body))])

(defn- subject->coords
  [subject]
  (map
   (fn [path] (partition 2 (map #(Double/parseDouble %) (split path #","))))
   (split (get-in subject [:draw :community :data]) #";")))

(defn static-map-communities
  "Given a subject has completed the map drawing question, create a static representation"
  [subject]
  (let [coords (subject->coords subject)]
    [:div.static-map
     (map
      (fn [path] [:input {:type "hidden"
                         :class "polygon"
                         :value (apply str (doall (flatten (interpose ";" (map #(interpose "," %) path))))) }])
      coords)
     map-canvas]))

(defn kml-map
  [url]
  [:div.kml-map
   [:input {:type "hidden"
            :value (if-not (= (appengine-environment-type) :development) (str url "?ts=" (System/currentTimeMillis)) url)
            :class "url"}]
   map-canvas])

(defelem yes-no-dk
  "Yes => No"
  [id]
  (bf/radio-group id
                  {:yes  "Yes"
                   :no "No"
                   :dk "Don't Recall"}))

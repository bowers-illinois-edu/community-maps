(ns community-maps.tags
  (:use [shanks core]
        hiccup.core
        [burp.core :only [add-class]]
        [burp.ring :only [wrap-burp]]
        [clojure.string :only [split join]])
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
     map-canvas]))

(defelem percentage
  "Allow the user to select from a list of percentages 0 to 100"
  [id]
  (bj/slider id))
         
(defelem agree-disagree
  "Strongly agree => strongly disagree"
  [id]
  (bf/radio-group id
                 {:strongly-agree "Strongly Agree"
                  :agree "Agree"
                  :neither "Neither agree nor disagree"
                  :disagree "Disagree"
                  :strongly-disagree "Strongly Disagree"}))

(defelem likelihood
  "Very likely => very unlikely"
  [id]
  (bf/radio-group id
                 {:very-likely "Very likely"
                  :likely "Likely"
                  :fifty-fity "Equally likely and unlikely (\"50/50\")"
                  :unlikely "Unlikely"
                  :very-unlikely "Very unlikely"}))

(def ethnic-political-groups
  {:black "Black"
   :white "White"
   :liberal "Liberal"
   :conservative "Conservative"
   :unemployed "Unemployed"
   :ndp "NDP"
   :chinese "Chinese"
   :east-indian "East Indian"
   :aboriginal "Canadian Aboriginal"
   :latin "Latin American"
   :other-asian "Other Asian"
   :quebecois "Bloq Quebecois"})

(defn percentage-of-community
  "Asks about the list of groups we are interested in"
  ([id prompt] (percentage-of-community id prompt true))
  ([id prompt percent]
     (question 
      prompt
      (f/with-group id
        [:table.groups 
         (doall
          (map
           (fn [[group-id group]]
             [:tr
              [:td.group group]
              [:td (str "0" (when percent "%"))]
              [:td {:width "60%"} (percentage group-id)]
              [:td (str "100" (when percent "%"))]])
           (shuffle (vec ethnic-political-groups))))]))))

(defn learn-about-composition
  "How did the R learn about his community."
  [id prompt]
  (multiple-choice
   id
   prompt
   {:observation "personal observation"
    :friends "friends and families"
    :news "news (tv, radio, online, paper)"
    :institutions "local institutions"
    :leaders "political leaders"
    :tv "television entertainment shows"}))

(defn seven-point-scale
  "Rate from 1 to 7"
  [id prompt]
  (add-class
   (question
    prompt
    (bf/radio-group id (map #(vector % %) (range 1 8)))
    [:br])
   "seven-point-scale"))

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
   [:input {:type "hidden" :value url :class "url"}]
   map-canvas])

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
      (add-class (button "Commencer à dessiner") "action start")
      (add-class
       (button {:style "display: none;"} "Arrêter de dessiner")
       "action stop")
      (add-class
       (button {:style "display: none;"} "Réinitialiser la carte")
       "action reset")]
     map-canvas]))

(defelem agree-disagree
  "Très en d'accord => Très en désaccord"
  [id]
  (bf/radio-group id
                  {:strongly-agree "Très en d'accord"
                   :agree "En accord"
                   :neither "Ni en accord ni en désaccord"
                   :disagree "En désaccord"
                   :strongly-disagree "Très en désaccord"}))

(defelem likelihood
  "Très probable => Très improbable"
  [id]
  (bf/radio-group id
                  {:very-likely "Très probable"
                   :likely "Probable"
                   :fifty-fity "Chances égales (\"50/50\")"
                   :unlikely "Improbable"
                   :very-unlikely "Très improbable"}))


(def ethnic-groups
  {:black "Noirs"
   :white "Blancs"
   :unemployed "Sans emploi"
   :chinese "Chinois"
   :east-indian "Indiens, Pakistanais, Sri Lankais, etc."
   :aboriginal "Autochtones canadiens"
   :latin "Latino-américains"
   :other-asian "Autres asiatiques (Coréen, Japonais, Philippins, etc.)"})

(defn political-groups
  "The political groups, with BQ if the subject is in Quebec"
  [subject]
  (let [all-see
        {:liberal "Parti Libéral"
         :conservative "Parti Conservateur"
         :ndp "Nouveau Parti Démocratique (NPD)"
         :green "Green Party"}]
    (if (from-quebec? subject)
      (assoc all-see :quebecois "Bloc Québécois")
      all-see)))


(defn political-groups-supporter
  "Adds 'supporter' to political groups"
  [subject]
  (map-vals #(str % " celui qui appuie le") (political-groups subject)))


;; take on "supporters" to the political groups
(defn ethnic-political-groups
  "Combines the ethnic and political groups, with BQ added for Quebec subjects"
  [subject]
  (merge
   ethnic-groups
   (map-vals #(str % "celui qui appuie le") (political-groups subject))))

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
  "Comment le répondant a-t-il appris sur sa communauté?"
  [id prompt]
  (let [opts {:observation "observation personnelle"
              :friends "amis et familles"
              :news "nouvelles (tv, radio, en ligne, papier)"
              :institutions "institutions locales (écoles, hôpitaux, bibliothèques, etc.)"
              :leaders "leaders politiques"
              :tv "Émissions de télévision de divertissement"}]
    (f/with-group id
      [:div.learn-composition
       [:div.mc
        (multiple-choice :learn (str prompt "Cochez svp tous les cas applicables") opts)]
       [:div.sc
        (single-choice :important "De ceux-ci, lequel/le était le plus important/e?" opts)]])))

(defn seven-point-scale
  "Notez de 1 à 7"
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
   (split (get subject :draw-community-data) #";")))

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
  "Oui => Non"
  [id]
  (bf/radio-group id
                  {:yes  "Oui"
                   :no "Non"
                   :dk "Ne me souviens pas"}))

(def no-back-button-msg
  (vector
   :div.no-back-button
   "Nous vous demandons de bien considérer vos réponses. Une fois que vous aurez cliqué sur le bouton 'Continuer', vous ne serez plus en mesure de revenir pour changer vos réponses. Ne vous servez pas du bouton 'Reculer d'une page' svp."))

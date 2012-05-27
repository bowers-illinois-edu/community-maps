(ns community-maps.screens.address
  (:use community-maps.tags
        shanks.core
        [burp.core :only [add-class]]
        clojure.contrib.strint)
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]))

(defscreen address
  [subject]
  
;;;Q1.
  (question
   "Dans un instant, nous vous demanderons de regarder sur la carte et de nous dire où vous habitez. Insérez svp votre code postal dans la boîte puis cliquez sur le bouton \"Trouver sur la carte\". Si la carte vous semble incorrect, essayez de réinsérer votre code postal (ou bien une intersection ou une adresse).  (Note: Ce genre d’enquête est anonyme. Votre adresse ne sera donc jamais utilisée pour d'autres fins que cette enquête.) "
            (f/with-group :address-finder
              [:div.map-find-address
               (f/hidden-field {:class "latlng"} :latlng)
               (f/text-field {:class "adresse"} :address (:address subject))
               (add-class (button "Trouver sur la carte") "update")
               [:div.map-canvas {:style "height: 400px; width: 100%;"}]
                 ])) ; if preloaded 


;;;Q2.	Question:
  [:div#how-long-lived
   (question "Depuis combien de temps demeurez-vous à votre domicile actuel?"
             (bf/radio-group :length-of-residence
                             {:less-than-one "Moins d'un an"
                              :x-years [:span (f/text-field :x-years) "années (insérez le nombre d'années svp)"]
                              :all-my-life "Toute ma vie"}))]
;;;Q3.	Question: 
  [:div#live-followup
   (question
    "Dans quelle municipalité et quelle province viviez-vous avant d’aménager dans votre domicile actuel? Sélectionnez la meilleure description possible et compléter la localisation ci-dessous."
    (bf/radio-group :other-residence
                    (map (fn [[k [v1 v2]]] [k [:span v1 " " [:span.followup v2 " " (f/with-group k (f/text-field :details))]]])
                         {:city ["Ailleurs dans la ville" "--S'il vous plaît dites-nous le code postal si vous vous en souvenez."]
                          :province ["Ailleurs dans la même province" " -- S'il vous plaît dites-nous le nom de la ville ou le code postal."]
                          :other ["Dans une autre province" " -- S'il vous plaît dites-nous le nom de la ville ou le code postal."]
                          :ex-canada ["À l'extérieur du Canada" " -- S'il vous plaît dites moi le nom du pays"]})))]

;;;Q4.	Question:
  (question 
   "Est-ce que vous ou votre famille êtes propriétaires, locataires ou autres?"
   (bf/radio-group
    :rent-own
    {:yes "Nous sommes propriétaires"
     :rent "Nous sommes locataires"
     :other "Nous avons un autre arrangement"}))

  (when (not (= "control" (:feel-close-to subject)))
    (list
     (question
      (<<
       "En ayant en tête où vous vivez, à quel point vous sentez-vous proche de ~{(when (not (= \"Canada\" (:feel-close-to subject))) \"your \")} ~{(:feel-close-to subject)}?")
      (bf/radio-group
       :feel-close-to-district
       {:very-close "Très proche"
        :close "Proche"
        :not-close "Pas proche"
        :not-close-at-all "Pas proche du tout"}))
     (question
      (<<
       "Si vous pouviez améliorer votre travail ou vos conditions de vie, à quel point seriez-vous prêt à déménager dans un/e autre 
              ~{(if (= \"Canada\" (:feel-close-to subject)) \"country\"  (:feel-close-to subject))}?")
      (bf/radio-group
       :willing-to-move
       {:very-willing "Très désireux"
        :fairly-willing "Assez désireux"
        :neutral "Ni l'un ni l'autre"
        :fairly-unwilling "Assez peu désireux"
        :very-unwilling "Pas désireux du tout"}))))

  no-back-button-msg)

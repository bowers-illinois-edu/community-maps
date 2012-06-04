(ns community-maps.screens.draw
  (:use community-maps.tags
        shanks.core
        [clojure.string :only [split join]])
  (:require
   [burp.forms :as bf]
   [hiccup.form-helpers :as f]))

(defscreen draw
  [subject]
  (let [[lat lng] (split
                   (get subject :address-address-finder-latlng)
                   #",")]
    (question
     (list
      [:span.required "Dessinez svp ce que vous pensez être votre communauté locale sur la carte."]
      [:ul (map #(vector :li %)
                ["Cliquez \"Commencer à dessiner\" pour commencer le dessin de votre communauté. (Il n'y a pas de \"bonne\" réponse et nous voulons que vous traciez ce qui vous vient en tête quand vous visualer votre communauté. Nous ne voulons surtout pas fournir une définition car nous voulons savoir ce qui est important et pertinent pour vous.)"
                 "Garder votre doigt sur le bouton de la souris pour tracer une ligne. Les formes ouvertes seront fermées si vous levez votre doigt pour quelques secondes."
                 "Vous pouvez faire autant de formes que vous le voulez."
                 "Même si nous avons centré la carte autour de votre code postal, sentez-vous libre d'utiliser le zoom ou de vous déplacer sur la carte si celle-ci ne contient pas tout le territoire que vous considérez comme partie de votre communauté locale. "
                 "Si vous avez besoin d'utilisez le zoom ou bien de bouger la carte après avoir commencé à dessiner, cliquez sur 'Arrêter de dessiner', ajustez la carte, et ensuite cliquez sur 'Commencer à dessiner' pour terminer le dessin de votre communaute"
                 "Si vous cliquez avec le bouton de gauche sur une zone complétée, vous aurez la chance d'effacer cette zone."
                 " Vous pouvez réinitialiser la carte pour effacer ce que vous y avez tracé."])]
      [:div#more-help [:a {:href "#"} "Plus d'aide"]
       [:div#more-help-hidden
        [:ul
         [:li "Certains ordinateurs ont une souris intégrée qui ne nécéssite qu'un contact du doigt pour activer la souris et maintenir l'action. Pour cesser de dessiner, toucher rapidement la surface."]
         [:li "Après avoir lâché le bouton de la souris, vous avez 5 secondes pour dessiner de nouveau de la position où vous êtes. Au bout de ces 5 secondes, ou si vous bougez la souris, la région se referme automatiquement. "]]]])
     (scribble-map :community lat lng (:drawing-zoom subject)))))

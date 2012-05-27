(ns community-maps.screens.follow-up-survey
  (:use community-maps.tags
        shanks.core)
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]))

(defscreen follow-up-survey
  [subject]
  (doall
   (map
    #(vector :p %)
    ["Merci d'avoir compléter ce questionnaire. Nous sommes reconnaissant du temps et l'effort que vous y avez mis. Vos réponses vont aider les chercheurs à mieux comprendre comment la société canadienne change et comment cela affectera des communautés comme la vôtre et d'autres à travers le pays."

     "Nous désirons vous rappeler encore une fois que vos réponses demeureront confidentielles et seront uniquement utilisées à des fins de recherche académique."

     "En conclusion, nous aimerions vous inviter à compléter un court questionnaire d'une durée de 5 à 7 minutes dans quelques semaines. Tous ceux qui accepteront de remplir ce court questionnaire additionel courent la chance de gagner un de quatre iPad 3 tirés au hasard. "]))
  (question
   "Si vous acceptez d'être contacté de nouveau, svp cochez la case ci-dessous:"
   (bf/labeled-checkbox :agree-to-followup "J'aimerais participer à une autre enquête."))) 

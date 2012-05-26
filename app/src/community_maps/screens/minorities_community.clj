(ns community-maps.screens.minorities-community
  (:use community-maps.tags
        community-maps.util
        shanks.core
        clojure.contrib.strint)
  (:require [hiccup.form-helpers :as f ]
            [burp.forms :as bf]))

(defscreen minorities-community
  [subject]
  (let [polgroups (conj (shuffle (vec (map-vals #(str % " supporters") (political-groups subject))))
                        [ :other
                         [:span "Une autre configuration. Expliquez svp:" (f/text-field :other-description)]])]
    (list
     (directions "Maintenant, jetez de nouveau un coup d’oeil à la carte de votre communauté locale.")
     (static-map-communities subject)

;;;Q22. Question:
     (question
      "Quand vous avez en tête votre communauté locale, diriez-vous qu’elle est surtout composée de membres de minorités ethniques, répartis de façon égale entre les deux ou d’une autre configuration?"
      (bf/radio-group :community-ethnic-makeup
                      {:white "Surtout de race blanche"
                       :ethnic [:span "Surtout des minorités raciales et ethniques. Expliquez pour chaque groupe svp:" (f/text-field :minorities-description)]
                       :half "Moitié-moitié"
                       :other [:span "Une autre configuration. Expliquez svp:" (f/text-field :other-description)]}))
;;;What is the largest nonwhite group?
;;;
;;;Q23. Question:
     (question
      "Quand vous avez en tête votre communauté locale, diriez-vous qu’elle est surtout:"
      (bf/radio-group
       :community-political-makeup
       polgroups))

;;;Q24a. Question:
     (question
      "Vos amis sont-ils surtout de race blanche, surtout membres de minorités ethniques, répartis de façon égale entre les deux ou d’une autre configuration?"
      (bf/radio-group
       :ethnic-friends-composition
       {:white "Surtout de race blanche"
        :ethnic [:span "Surtout des minorités raciales et ethniques. Expliquez pour chaque groupe svp:" (f/text-field :minorities-description)]
        :half "Environ moitié moitié"
        :other [:span "" (f/text-field :other-description)]}))
;;;What is the largest nonwhite group?

;;;Q25. Question:
     (question
      (<< "Vos amis sont-ils surtout ~{(apply str (interpose \", \" (vals (political-groups subject))))} ou d’une autre configuration?")
      (bf/radio-group
       :political-friends-composition
       (conj
        (vec (map (fn [[k v]] [k (<< "Surtout ~{v}")]) (shuffle (vec (political-groups subject)))))
        [ :other [:span "Une autre configuration. Expliquez svp:" (f/text-field :other-description)]])))
     ))
  (group-sliders
   subject
   :group-feeling-thermometers
    (list [:p "Nous aimerions également connaître vos sentiments à l’égard de certains groupes de la société canadienne. Pour chacun des groupes suivants, nous vous demandons d'évaluer ces groupes à l’aide de ce qu’on appelle un thermomètre de sentiment."]
         [:p "Les valeurs entre 50 et 100 degrés signifient que vous avez une opinion favorable ou positive à l’égard du groupe en question; les valeurs entre 0 et 50 degrés signifient que vous avez une opinion défavorable ou  négative envers le groupe en question; si vous n’avez pas une opinion particulièrement favorable ou défavorable envers un groupe donné, vous pouvez donner un score de 50."])
   "0" "100")
  
  no-back-button-msg)

(ns community-maps.screens.own-community
  (:use community-maps.tags
        shanks.core)
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]))

(defscreen own-community
  [subject]

  (directions "Pour les prochaines questions, nous nous référerons à la zone en surbrillance.")
  (static-map-communities subject)
  (when (:on-your-mind-question subject)
    (list
     (multiple-choice
      :on-your-mind
      "À quoi pensiez-vous lorsque vous dessiniez votre \"communauté locale\"? Cocher toutes les options qui correspondent à votre réponse:"
      {:weekly "Les gens et les lieux que vous voyez sur une base régulière"
       :people-like-you "Des gens comme vous"
       :local-places "Votre épicerie, bibliothèque, bureau de poste ou autres endroits que vous visitez régulièrement"
       :family "Famille et amis"
       :voting "Les gens et les lieux auxquels vous pensez quand vous aller voter au cours d'une élection"
       :neighbourhood "Votre quartier"
       :newspapers "Ce que vous lisez dans les journaux"
       :tv "Ce que vous voyez à la télévision ou sur internet"})
     (f/with-group :on-your-mind
       (bf/labeled-checkbox
        :other [:span "Autre, spécifiez svp " (f/text-field :other-explanation)]))))
;;;Q6.  Question
  (group-sliders
   subject
   :community-percentage
   "À combien estimez-vous le pourcentage de la population dans cette communauté qui est:")

;;;Q7.  Question:
  (learn-about-composition
   :community-composition
   "Comment en arrivez-vous à ces estimations quant à la composition de votre communauté locale?")

  (directions "Voici quelques affirmations au sujet des gens de votre communauté locale. Pour chacune de ces affirmations, dites-nous à quel point vous êtes en accord ou en désaccord.")

;;;Q9, Q10, Q11

  (doall
   (map
    (fn [[id prompt]] (question prompt (agree-disagree id)))
    {:help "Les gens ici sont prêts à aider les autres membres de leur communauté."
     :get-along "Les gens dans ma communauté n’ont généralement pas de bonnes relations entre eux."
     :share-values "Les gens de ma communauté ne partagent pas les mêmes valeurs."}))

  (directions "Pour chaque affirmation suivante, dites-nous s'il est très probable, probable, improbables ou très improbable que les membres de votre communauté locale agissent de la façon suivante.")

;;;Q12., Q13.
  (doall
   (map
    (fn [[id prompt]] (question prompt (likelihood id)))
    {:graffiti "Si des enfants faisaient des graffitis sur les murs d’une résidence ou d’un édifice, à quel point est-ce probable que des membres de votre communauté fassent quelque chose pour que cela cesse?"
     :community-organize "Supposons que dues à des coupures budgétaires, la bibliothèque publique la plus proche de chez vous risque la fermeture. À quel point est-ce probable que les membres de votre communauté s’organisent et posent des actions pour maintenir celle-ci ouverte?"}))

;;;Participation Questions
  (directions "Nous aimerions savoir si vous avez été impliqué dans votre communauté récemment.")

  (doall
   (map
    (fn [[id prompt]] (question prompt (yes-no-dk id)))
    {:informalpartic "Au cours des 12 derniers mois, avez-vous travaillé avec d'autres sur des enjeux touchant votre communauté ou vos écoles?"
     :meetingpartic "Au cours des 12 derniers mois, avez-vous assisté à une rencontre ayant pour thème un enjeu touchant votre communauté ou vos écoles?"}
    ))
  
  no-back-button-msg)

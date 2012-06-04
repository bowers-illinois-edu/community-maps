(ns community-maps.screens.everything
  (:use community-maps.tags
        shanks.core
        [burp.core :only [add-class]]
        clojure.contrib.strint)
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]
            [community-maps.gis :as gis]))

(defscreen randomized-district
  [subject]
  (list
  ;;; District Map Related Questions (if we can't look up the
  ;;; district, they are skipped entirely.
   (let [dst (:display-district subject)
         dst-name (get gis/*districts* dst)
         district-id (gis/get-subject-district-id subject dst)
         prompt (if (= "canada" dst) "Canada" (str "votre" dst-name))]
     (when (and (not (= 0 district-id)) (not (= "" district-id)))
       (list
        (list
         (directions
          (<< "Regarder svp la carte. La zone en surbrillance montre ~{prompt}~{(when (get gis/extended-descriptions dst) (str \", \" (gis/extended-descriptions dst)))}.")
          (<< "En vous référant à la carte de ~{prompt}, nous aimerions vous poser une série de questions semblables aux précédentes:"))
         (if (= "canada" dst)
           [:img {:src "/canada.jpg"}]
           (kml-map (gis/kml-url dst district-id))))

      ;;;Q14.   Question:
        (group-sliders
         subject
         :census-community
         (<< "Selon vos meilleures estimations, quel pourcentage de la population de ~{prompt} est:"))

      ;;;Q15.   Question:
        (learn-about-composition
         :census-composition
         (<< "Comment en arrivez-vous à ces estimations quant à la composition de ~{prompt}?"))

      ;;;Q16.   Question:
        (question
         (<< "En général, aimez-vous ou non ~{prompt} dans lequel vous habitez?")
         (bf/radio-group
          :like-dislike-census
          {:like-alot "Aime beaucoup"
           :like "Aime"
           :dislike "N'aime pas"
           :dislike-alot "N'aime pas du tout"}))

      ;;;Q17.   Question:
        (yes-no :census-feel-community
                (<< "En général, pensez-vous que les gens qui vivent dans votre ~{prompt} partage un sentiment de communauté?"))

        (question
         (<< "Au cours des 5 dernières années, pensez-vous que ~{prompt} est devenu plus diversifié ethniquement, moins diversifié ou encore est demeuré le même.")
         (bf/radio-group
          :perceived-diversity-chang
          {:more "Plus diversifiée"
           :less "Moins diversifiée"
           :same "Rester la même"}))
;;;Q18. Question:
        (question
         (<< "Certains leaders politiques affirment qu’au cours des dix prochaines années, le pourcentage de la population de votre [province/municipalité/aire de diffusion] issu des minorités ethniques va [beaucoup augmenter/beaucoup diminuer]. Pensez-vous que ce soit une bonne ou une mauvaise chose si cela se concrétisait?")
         (bf/radio-group :ethnic-growth {:good "Bonne chose" :neutral "Ni bonne ni mauvaise" :bad "Mauvaise chose"}))


;;;What is the largest nonwhite group?

        (directions
         "En ayant en tête le Canada dans son ensemble, nous aimerions que vous nous disiez si vous vous sentez particulièrement proche de ce groupe -- si vous sentez que les membres de ce groupe partagent avec vous des idées, intérêts et sentiments communs.")
;;;
;;;Q21. Question:
;;;
        (let [mc (multiple-choice
                  :close-to-group
                  "Cochez svp pour tous les groupes desquels vous vous sentez proche"
                  (merge
                   (dissoc (ethnic-political-groups subject) :other-asian)
                   {:local-community "Les gens de votre communauté locale"
                    :census-community (<< "Les gens dans ~{prompt}")}))]
          (assoc-in mc [2 1]
                    (concat
                     (second (first (get-in mc [2 1])))
                     [(f/with-group "autre asiatique" (bf/labeled-checkbox "other-asian" "Autres asiatiques (Coréen, Japonais, Philippins, etc.)"))]))))))

   (question
     "Sur des questions touchant les enjeux sociaux et politiques, certaines personnes se voient principalement comme Blanches, Chinoises ou Noires. Cela a un impact important sur la façon dont ils se perçoivent. D’autres ne partagent pas cette perception. Sur des questions touchant les enjeux sociaux et politiques, quelle importance occupe votre appartenance ethnique dans votre perception de vous-même?"
    (bf/radio-group
     :has-ethnic-identity
     {:very-important "Très important"
      :somewhat-important "Plutôt important"
      :not-very-important "Pas très important"
      :not-important "Pas important du tout"}))

   ;; Party ID and vote choice questions
   (question 
    "En politique fédérale, vous considérez-vous habituellement:"
    (bf/radio-group
     :party-id
     (conj (shuffle (vec (political-groups-supporter subject))) [:none "Aucun de ceux-ci"])))

   [:div.election-choice.national-election-choice
    (add-class
     (yes-no :national-election "Avez-vous voté à l'élection fédérale de mai 2011?")
     :did-vote)
    (add-class
     (question
      "Pour quel parti avez-vous voté?"
      (bf/radio-group :national-election-choice
                      (conj
                       (shuffle (vec (political-groups subject)))
                       [:other "Un autre parti"])))
     :vote-choice)]

   [:div.election-choice.provincial-election-choice
    (add-class
     (yes-no :provincial-election "Avez-vous voté à la récente élection provinciale?")
     :did-vote)
    (add-class
     (question
      "Pour quel parti avez-vous voté?"
      (bf/radio-group :provincial-election-choice
                      (conj
                       (shuffle (vec (political-groups subject)))
                       [:other "Un autre parti"])))
     :vote-choice)]

   (when (gis/from-alberta? subject)
     (vector :div.election-choice.alberta-election
      (add-class
       (question
        "Au meilleur de votre connaissance, pensez-vous voter à l'élection provinciale du 23 avril?"
        (bf/radio-group :alberta-vote-0423
                        {:yes "Oui, je m'attends à voter"
                         :no "Non, je ne m'attends pas à voter"}))
       :did-vote)
      (add-class
       (question "Pour quel parti avez-vous voté?"
                 (bf/radio-group :alberta-vote-0423-choice
                                 (conj
                                  (shuffle 
                                   [[:liberal "Le Parti Libéral"]
                                    [:conservative "Le Parti Progressiste-Conservateur"]
                                    [:wildrose "Le Parti Wild Rose"]
                                    [:ndp "Le Nouveau Parti Démocratique (NPD)"] 
                                    [:alberta "Le Parti Alberta"]])
                                  [:other "Un autre parti"])))
       :vote-choice)))
   
   (question
    [:div
     (when-not (= "Aucun" (:minority-projection subject))
       [:p
        "Selon les projections du recensement, le nombre de minorités visibles va atteindre"
        (:minority-projection subject)
        "Le pourcentage de la population canadienne au cours des dix prochaines années, dû en grande partie à l'immigration"])
     [:p "Pensez-vous que le nombre d’immigrants à qui on accorde le droit de venir vivre au Canada devrait augmenter un peu, augmenter beaucoup, diminuer un peu, diminuer beaucoup, ou demeurer le même?"]]
    (bf/radio-group
     :increase-immigration
     {:increase-much "A augmenté beaucoup"
      :increase-little "A augmenté un peu"
      :same "Est resté le/la même"
      :decrease-little "diminué un peu"
      :decrease-much "A diminué beaucoup"})))

  (question
   "Êtes-vous en accord ou en désaccord avec l'affirmation suivante: Parler français ou anglais devrait être une condition pour immigrer au Canada."
   (agree-disagree :req-lang-immigration))
  no-back-button-msg)


(defscreen racial-ethnic [subject]
;;;Q29. Question:


  (directions
   "Pour chaque affirmation suivante, dites-nous l'intensité de votre accord ou de votre désaccord.")
;;;
;;;Q 30, 31. Q32, Q33, Q34, Q35
  (doall
   (map
    (fn [[k p]] (question p (agree-disagree k)))
    {:melting-pot
     "Cela est mieux pour le Canada que les différents groupes ethniques maintiennent leurs cultures distinctes, formant ainsi une mosaïque culturelle plutôt qu'un mélange."

     :happier-with-other-groups
     "Les gens sont généralement plus heureux quand ils vivent et socialisent avec d'autres d'origines ethniques et raciales différentes."

     :special-favors
     "Les Irlandais, Italiens, Juifs et de nombreuses autres minorités ont surmonté les préjudices auxquels ils ont fait face et se sont élevées dans l’échelle sociale. D’autres minorités devraient faire la même chose, et ce sans traitement de faveur."

     :try-harder
     "C’est vraiment une question de manque d’effort. Si les minorités ethniques essayaient davantage, elles seraient aussi riches que le sont les Blancs."

     :gov-attention
     "Les représentants du gouvernement accordent généralement moins d’attention aux demandes ou plaintes des membres de minorités visibles qu’à celles de personnes de race blanche."}))

;;;Q36. Question:
  (question
   (<< "Comment vous sentireriez-vous si un proche parent planifiait de marier une personne ayant des ~{(:outgroup-marry subject)} différentes des vôtres?")
   (bf/radio-group :marry-ethnic
                   {:very-uneasy "Très mal à l'aise"
                    :somewhat-uneasy "Un peu mal à l'aise"
                    :not-uneasy "Pas mal à l'aise du tout"}))

;;;Q37. Question:
  (question
   "Comment vous sentireriez-vous si un proche parent planifiait de marier une personne ayant de fortes opinions politiques différentes des vôtres?"
   (bf/radio-group :marry-political
                   {:very-uneasy "Très mal à l'aise"
                    :somewhat-uneasy "Un peu mal à l'aise"
                    :not-uneasy "Pas mal à l'aise du tout"}))
  no-back-button-msg)

(defscreen racial-conflict [subject]
  (question
   "En général, aimez-vous ou non le quartier dans lequel vous habitez?"
   (bf/radio-group :like-neighbourhood
                   {:like-alot "Aime beaucoup"
                    :like "Aime"
                    :dislike "N'aime pas"
                    :dislike-alot "N'aime pas du tout"}))
  (question
   "À quel point êtes-vous inquiet à propos de la sécurité dans votre quartier?"
   (bf/radio-group :safe-neighbourhood
                   {:worried "Inquiet"
                    :somewhat-worried "Un peu inquiet"
                    :not-very-worried "Pas très inquiet"
                    :not-at-all-worried "Pas du tout inquiet"}))

  (question
   "Si vous pouviez trouver un logement que vous aimez, préféreriez-vous vivre dans un quartier où vos voisins partagent vos opinions et valeurs poltiques, ou qu'il possède un large éventail d'opinions et de valeurs poltiiques, ou cela n'a aucune importance pour vous?"
   (bf/radio-group
    :housing-political
    {:share "La plupart partage mes opinions et valeurs politiques"
     :diversity "La plupart possède un large éventail d'opinions et de valeurs politiques"
     :not-important "Ceci n'est pas important pour moi"}))

  (question
   "Quand est-il des groupes ethniques dans votre quartier? Si vous pouviez trouver un logement que vous aimez, préféreriez-vous vivre dans un quartier qui vous ressemble en termes ethnique et racial, ou plutôt où on trouve un mélange d'origines ethniques et raciales, ou cela n'a aucune importance pour vous?"
   (bf/radio-group
    :housing-ethnic
    {:same "Partage en grande partie mes origines ethniques et raciales"
     :diversity "Surtout un mélange d'origines ethniques et raciales"
     :not-important "Ceci n'est pas important pour moi"}))

  (directions
   "Voici quelques affirmations. Pour chacune d'entre elles, dites-nous à quel point vous êtes en accord ou en désaccord.")

  (doall
   (map
    (fn [[key prompt]] (question prompt (agree-disagree key)))
    {:taxes-increased
     (<< "Les taxes devraient être augmentées ~{(:taxes-increased subject)} pour améliorer le transport public et les routes.")

     :french-language
     (<< "~{(:french-language subject)} devrait traiter le français comme toutes les autre langues minoritaires.")

     :anti-racism
     (<< "Les écoles dans ~{(:anti-racism-unit subject)} devraient se voir obliger d'élaborer des programmes ~{(:anti-racism-cirriculum subject)}.")}))
  
  (single-choice {:id "étudiant/e ayant un emploi"} :employed
                 "Êtes-vous présentement travailleur/euse ou encore étudiant/e"
                 {:employed "Je travaille"
                  :student "Je suis un/e étudiant/e"
                  :retired "Je suis un/e retraité/e"
                  :unemployed "Je suis sans emploi"})
  [:div#employment-follow-up
   (question
    "Pourriez-vous nous dire où vous travaillez ou étudiez? Fournissez svp un code postal ou, si vous ne le connaissez pas, une intersection ou une adresse."
    (f/text-field :work-study-address))

;;;Q20. Question:
   (question
    "Vos collègues de travail (ou d’études) sont-ils surtout Blancs, issus de minorités ethniques, répartis de façon égale entre les deux ou d’une autre configuration?"
    (bf/radio-group :work-ethnicity
                    {:white "Surtout de race blanche"
                     :ethnic [:span "Surtout des minorités raciales et ethniques. Expliquez pour chaque groupe svp:" (f/text-field :ethnic-description)]
                     :half "Environ moitié moitié"
                     :other [:span "Une autre configuration. Expliquez svp:" (f/text-field :other-description)]}))]
  no-back-button-msg)

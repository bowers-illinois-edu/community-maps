(ns community-maps.screens.minorities-community
  (:use community-maps.tags
        shanks.core
        clojure.contrib.strint)
  (:require [hiccup.form-helpers :as f ]
            [burp.forms :as bf]))

(defscreen minorities-community
  [subject]
  (let [polgroups (conj (shuffle (vec (political-groups subject)))
                        [ :other
                         [:span "Some other mixture. Please explain: " (f/text-field :other-description)]])]
    (list
     (directions "Now let’s look at the map of your local community again.")
     (static-map-communities subject)

;;;Q22. Question:
     (question
      "Thinking about your local community again: is it mostly white, mostly racial and ethnic minorities, about half and half, or some other mixture of people?"
      (bf/radio-group :community-ethnic-makeup
                      {:white "Mostly white"
                       :ethnic [:span "Mostly racial and ethnic minorities. Please explain which groups: " (f/text-field :minorities-description)]
                       :half "About half and half"
                       :other [:span "Some other mixture. Please explain: " (f/text-field :other-description)]}))
;;;What is the largest nonwhite group?
;;;
;;;Q23. Question:
     (question
      "Thinking about your local community, is it mostly:"
      (bf/radio-group
       :community-political-makeup
       polgroups))

;;;Q24a. Question:
     (question
      "Now thinking more generally, are your friends mostly white, mostly racial and ethnic minorities, about half and half, or some other mixture of people?"
      (bf/radio-group
       :ethnic-friends-composition
       {:white "Mostly white"
        :ethnic [:span "Mostly racial and ethnic minorities. Please explain which groups: " (f/text-field :minorities-description)]
        :half "About half and half"
        :other [:span "Some other mixture. Please explain: " (f/text-field :other-description)]}))
;;;What is the largest nonwhite group?

;;;Q25. Question:
     (question
      (<< "Thinking more generally, are your friends mostly ~{(apply str (interpose \", \" (vals (political-groups subject))))} or some other mixture?")
      (bf/radio-group
       :political-friends-composition
       (conj
        (vec (map (fn [[k v]] [k (<< "Mostly ~{v}")]) (shuffle (vec (political-groups subject)))))
        [ :other [:span "Some other mixture. Please explain:" (f/text-field :other-description)]])))
     ))
  (group-sliders
   subject
   :group-feeling-thermometers
   (list [:p "Now we would like to get your feelings about some groups in Canadian society. For each of the following groups, we would like you to rate it with what we call a feeling thermometer."]
         [:p "Ratings between 50 degrees and 100 degrees mean that you feel favorably and warm toward the group; ratings between 0 and 50 degrees mean that you don't feel favorably towards the group and that you don't care too much for that group. If you don't feel particularly warm or cold toward a group you would rate them at 50 degrees."])
   "0" "100")
  
  no-back-button-msg)

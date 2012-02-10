(ns community-maps.screens.minorities-community
  (:use community-maps.tags
        shanks.core)
  (:require [hiccup.form-helpers :as f ]
            [burp.forms :as bf]))

(defscreen minorities-community
  [subject]
  (let [polgroups (conj (shuffle (vec (political-groups subject)))
          [ :other
           [:span "Some other mixture. Please explain: " (f/text-field :other-description)]])]
    (list (static-map-communities subject)

          (directions "Now let’s look at the map of your local community again.")

          
;;;Q22.	Question:
          (question 
           "Thinking about your local community again: is it mostly white, mostly racial and ethnic minorities, about half and half, or some other mixture of people?"
           (bf/radio-group :community-ethnic-makeup
                           {:white "Mostly white"
                            :ethnic "Mostly racial and ethnic minorities"
                            :half "About half and half"
                            :other [:span "Some other mixture. Please explain: " (f/text-field :other-description)]}))
;;;What is the largest nonwhite group?
;;; 
;;;Q23.	Question: 
          (question
           "Thinking about your local community, is it mostly:"
           (bf/radio-group
            :community-political-makeup
            polgroups))
          
;;;Q24.	Question:
          (yes-no :ethnic-friends "Are any of your friends of a different race or ethnic background than you?")
;;; 
;;;If yes, then Q24a
;;;Q24a. Question:
          (question 
           "Are your friends mostly white, mostly racial and ethnic minorities, about half and half, or some other mixture of people?"
           (bf/radio-group
            :ethnic-friends-composition
            {:white "Mostly white"
             :ethnic "Mostly racial and ethnic minorities"
             :half "About half and half"
             :other [:span "Some other mixture. Please explain:" (f/text-field :other-description)]}))
;;;What is the largest nonwhite group?

;;;Q25.	Question:
          (question 
           (str "Are your friends mostly "
                (apply str (interpose ", " (vals (political-groups subject))))
                " or some other mixture?")
           (bf/radio-group
            :political-friends-composition
            (conj
             (vec (map (fn [[k v]] [k (str "Mostly " v)]) (shuffle (vec (political-groups subject)))))
             [ :other [:span "Some other mixture. Please explain:" (f/text-field :other-description)]])))
)))

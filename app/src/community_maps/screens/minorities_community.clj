(ns community-maps.screens.minorities-community
  (:use community-maps.tags
        shanks.core)
  (:require [hiccup.form-helpers :as f ]
            [burp.forms :as bf]))

(defscreen minorities-community
  [subject]
  (static-map-communities subject)

  (directions "Now let’s look at the map of your local community again.")

  
;;;Q22.	Question:
  (question 
   "Thinking about your local community: is it mostly white, mostly ethnic minorities, about half and half, or some other mixture of people?"
   (bf/radio-group :community-ethnic-makeup
                   {:white "Mostly white"
                    :ethnic "Mostly ethnic minorities"
                    :half "About half and half"
                    :other [:span "Some other mixture. Please explain: " (f/text-field :other-description)]}))
;;;What is the largest nonwhite group?
;;; 
;;;Q23.	Question: 
  (single-choice
   :community-political-makeup
   "Thinking about your local community, is it mostly:"
   (assoc (political-groups subject)
     :other
     [:span "Some other mixture. Please explain: " (f/text-field :other-description)]))
  
;;;Q24.	Question:
  (yes-no :ethnic-friends "Are any of your friends of a different race or ethnic background than you?")
;;; 
;;;If yes, then Q24a
;;;Q24a. Question:
  (question 
   "Are your friends mostly white, mostly ethnic minorities, about half and half, or some other mixture of people?"
   (bf/radio-group
    :ethnic-friends-composition
    {:white "Mostly white"
     :ethnic "Mostly ethnic minorities"
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
     (vec (map (fn [[k v]] [k (str "Mostly " v)]) (political-groups subject)))
     [ :other [:span "Some other mixture. Please explain:" (f/text-field :other-description)]]))))

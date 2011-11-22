(ns community-maps.screens.own-community
  (:use community-maps.tags
        shanks.core)
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]))

(defscreen own-community
  [subject]
  
  (static-map-communities subject)
;;; 
;;; 
;;;Q5a. Map-drawing question
;;; 
;;;[IF HAVING TROUBLE: If you are having trouble with the drawing, please name major cross streets, parks, stores or other landmarks that you think of as boundaries of your local community.]
;;; 
  ; Some inter-question directions:
  (directions "Now, for these next few questions we’ll be referring to the
areas you highlighted")

;;;Q6.	Question
  (group-sliders
   :community-percentage
   "Just your best guess – what percentage of the population of this community is:")
;;; 
;;;Q7.	Question:
  (learn-about-composition
   :community-composition
   "How did you learn about the composition of your local community?")
;;;[IF R ASKS: Composition is the percentage of whites, unemployed, etc. in your local community.]
;;; 
;;;if more than one is checked, which is the main source of information.
;;;Does this differ by target (race, party, unemployment)

  (directions "Here are some statements about things that people in your local community that you have drawn on this map [REFERRING TO MAP THEY DREW ON] may or may not do. For each of these statements, please tell me whether you strongly agree, agree, neither agree nor disagree, disagree, or strongly disagree.")

;;;Q9, Q10, Q11	

  (doall
   (map
    (fn [[id prompt]] (question prompt (agree-disagree id)))
    {:help "People around here are willing to help others in their community."
     :get-along "People in this community generally don’t get along with each other."
     :share-values "People in this community do not share the same values."}))

  (directions "For each of the following, please tell me if it is very likely, likely, unlikely or very unlikely that people in your community would act in the following manner.")

;;;Q12., Q13.
  (doall
   (map
    (fn [[id prompt]] (question prompt (likelihood id)))
    {:graffiti "If some children were painting graffiti on a local building or house, how likely is it that people in your community would do something about it?"
     :community-organize "Suppose that because of budget cuts the fire station or library closest to your home was going to be closed down by the city. How likely is it that community residents would organize to try to do something to keep the fire station open?"})))

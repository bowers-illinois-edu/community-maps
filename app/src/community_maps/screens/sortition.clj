(ns community-maps.screens.sortition
  (:use community-maps.tags
        shanks.core
        [clojure.string :only [split join]])
  (:require [hiccup.form-helpers :as f ]
            [burp.forms :as bf]
            [community-maps.gis :as gis]))


(defscreen sortition-election-district-drawining
  [subject]
  (let [[lat lng] (split (get-in subject [:address :address-finder :latlng]) #",")
        dst (if (= "federal" (:draw-district-display subject))
              (gis/get-subject-district-id subject "fed")
              0)]
    (question
     (list
      "For this next map, we are asking you to draw again. This time, imagine that you are in-charge of drawing a district that will send a representative to the House of Commons."

      (str
       (if (= "election" (:election-sortition subject))
         " The representative will be elected by the citizens of the district you draw."
         " Instead of an election, the representative will be selected at random from the population, similar to jury selection.")
       " The representative will serve a standard term in the House of Commons (4 years or until dissolution of parliment).")
      
      (when (and (not (= 0 dst)) (= "federal" (:draw-district-display subject))) "The map displays your current riding to give you a sense of scale. You do not have to follow this district for your drawing, though you may if you wish.")

      (when (= "community" (:draw-district-display subject)) "The map displays the community you created on a previous step of this survey. You do not have to follow this community for your drawing, though you may if you wish."))

     (if (and (not (= 0 dst)) (= "federal" (:draw-district-display subject)))
       (scribble-map :community lat lng (gis/kml-url "fed" dst))
       (scribble-map :community lat lng)))))


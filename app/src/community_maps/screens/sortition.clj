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
    (list (directions
           "For this next map, we are asking you to draw again. This time, imagine that you are in-charge of drawing a district that will send a representative to the House of Commons."

           (str
            (if (= "election" (:election-sortition subject))
              " <strong>The representative will be elected by the citizens of the district you draw.</strong>"
              " <strong>Instead of an election, the representative will be selected at random from the population of the district, similar to jury selection.</strong>")
            " The representative will serve a standard term in the House of Commons (4 years or until dissolution of parliment)."))

          (question
           "Please draw the district that you think would result in your getting the best possible representation in the House of Commons."
           (when (and (not (= 0 dst)) (= "federal" (:draw-district-display subject))) "The map displays your current riding to give you a sense of scale. You do not have to follow this district for your drawing, though you may if you wish.")

           (if (and (not (= 0 dst)) (= "federal" (:draw-district-display subject)))
             (scribble-map :community lat lng (gis/kml-url "fed" dst))
             (scribble-map :community lat lng))))))


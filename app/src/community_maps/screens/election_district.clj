(ns community-maps.screens.election-district
  (:use shanks.core
        community-maps.tags))

(defscreen election-district-drawing
  [subject]
  (directions
   (if (= "sortition" (:election-district-type subject))
     "Sortition"
     "Election")))

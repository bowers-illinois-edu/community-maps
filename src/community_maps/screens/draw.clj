(ns community-maps.screens.draw
  (:use community-maps.tags
        shanks.core))

(defscreen draw-on-map
  [subject]
  (question "Please draw on the map"
            (scribble-map :test-scribble 40.1105556 -88.2072222))
  (rank-options :icecream "Rank the following ice cream flavors:"
                {:choc "Chocolate"
                 :van "Vanilla"
                 :straw "Strawberry"
                 :rock "Rocky Road"}))

(ns community-maps.screens.draw
  (:use community-maps.tags
        shanks.core
        [clojure.string :only [split join]]))

(defscreen draw
  [subject]
  (let [[lat lng] (split
                   (get-in subject [:address :address-finder :latlng])
                   #",")]
    (question "Please draw on the map"
              (scribble-map :test-scribble lat lng))))

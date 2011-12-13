(ns community-maps.screens.draw
  (:use community-maps.tags
        shanks.core
        [clojure.string :only [split join]]))

(defscreen draw
  [subject]
  (let [[lat lng] (split
                   (get-in subject [:address :address-finder :latlng])
                   #",")]
    (question
     (list
      [:span.required "Please Draw Your Local Community on the Map. "]
      [:ul (map #(vector :li %)
                ["Click \"Start Drawing\" to begin drawing your community."
                 "Hold down the mouse button to make a line. Open shapes will be enclosed if you lift your mouse for a few 		seconds."
                 "You may make as many shapes as you like."
                 "Even though we have centered the map on your postal code, you should feel free to zoom in or out and to move 		the map if it does not contain the areas that you consider your local community."
                 "If you left click on a completed shape you will have the chance to delete that community."
                 "You may Erase the Map to delete all of your shapes."])])
              (scribble-map :community lat lng))))

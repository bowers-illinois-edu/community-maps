(ns community-maps.screens.draw
  (:use community-maps.tags
        community-maps.previous-survey
        shanks.core
        [clojure.string :only [split join]])
  (:require
   [burp.forms :as bf]
   [hiccup.form-helpers :as f]))

(defscreen draw
  [subject]
  (let [[vcid zoom lat lng] (drawing-data-pid (:pid subject))]
    (question
     (list
      [:span.required "Please draw what you think of as your Local Community on the map. "]
      [:ul (map #(vector :li %)
                ["Click \"Start Drawing\" to begin drawing your community. (There is no \"right\" answer, and we would like you to draw whatever comes to mind when you think of your \"local community.\" We specifically do not provide a definition because we want to learn from you what is important and relevant.)"
                 "Hold down the mouse button to make a line. Open shapes will be enclosed if you lift your mouse for a few 		seconds."
                 "You may make as many shapes as you like."
                 "Even though we have centered the map on your postal code, you should feel free to zoom in or out and to move 		the map if it does not contain the areas that you consider your local community."
                 "If you need to zoom in or out, or move the map, after you have begun drawing, click Stop Drawing, adjust the map, and then click Start Drawing to finish drawing your community."
                 "If you left click on a completed shape you will have the chance to delete that community."
                 "You may Reset Map to delete all of your shapes to start over."])]
      [:div#more-help [:a {:href "#"} "More help"]
       [:div#more-help-hidden
        [:ul
         [:li "Some trackpads use a single tap to indicate mouse down and then automatically keep the mouse button, as if you had your finger on the button. To release the drawing, tap the trackpad again to signal that you are done drawing."]
         [:li "After releasing the mouse button, you have 5 seconds to start drawing again from that position. If 5 seconds elapse, or you move the mouse, the region automatically close."]]]])
     (scribble-map :community lat lng zoom))))

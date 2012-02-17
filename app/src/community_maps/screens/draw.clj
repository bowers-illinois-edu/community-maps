(ns community-maps.screens.draw
  (:use community-maps.tags
        shanks.core
        [clojure.string :only [split join]])
  (:require
   [burp.forms :as bf]
   [hiccup.form-helpers :as f]))

(defscreen draw
  [subject]
  (let [[lat lng] (split
                   (get-in subject [:address :address-finder :latlng])
                   #",")]
    (question
     (list
      [:span.required "Please draw what you think of as your Local Community on the map. "]
      [:ul (map #(vector :li %)
                ["Click \"Start Drawing\" to begin drawing your community."
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
     (scribble-map :community lat lng (:drawing-zoom subject))))
  (when (:on-your-mind-question subject)
    (list
     (multiple-choice
      :on-your-mind
      "What were you thinking about as you were drawing your \"local community\"? Check all that apply:"
      {:weekly "People or places you see on a weekly basis"
       :people-like-you "People like you"
       :local-places "Your grocery store, library, post office, church, or other places you visit on a regular basis"
       :family "Family and friends"
       :voting "People or places you think about when you go vote in an election"
       :neighborhood "Your neighborhood"
       :newspapers "What you read about in newspapers?"
       :tv "What you see on television or the internet?"})
     (f/with-group :on-your-mind
       (bf/labeled-checkbox
        :other [:span "Other, please specify: " (f/text-field :other-explanation)])))))

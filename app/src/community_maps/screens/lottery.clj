(ns community-maps.screens.lottery
  (:use community-maps.tags
        shanks.core)
  (:require
   [burp.forms :as bf]
   [hiccup.form-helpers :as f]))

(defscreen lottery
  [subject]
  (directions
   "Thank you for completing this survey. As a thank you to our participants, we are giving away 3 Apple iPad (3rd generation).")
  (yes-no :enter "I would like to be entered in the drawing"))

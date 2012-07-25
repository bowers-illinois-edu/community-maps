(ns community-maps.screens.vmexperiment
  (:use shanks.core
        [community-maps tags util]
        clojure.contrib.strint)
  (:require
   [burp.forms :as bf]
   [hiccup.form-helpers :as f]))

(defscreen vmexperiment
  [subject]
  (question
   (str "According to projections by the census, and largely as a result of immigration, the number of visible minorities is going to reach 30% of the Canadian population in the next 20 years. This is about double the current size. "
    (when (= "threat" (:vismin-experiment subject))
      "Many political leaders worry that this rapid change threatens Canadian identity. ")
    "<br/><br/>Do you think the number of immigrants from foreign countries who are permitted to come to Canada to live should:")
   (bf/radio-group
    :increase-immigration
    { :increase-little "Increase a little"
     :increase-lot "Increase a lot"
     :decrease-little "Decrease a little"
     :decrease-lot "Decrease a lot"
     :same "Stay about the same"})))

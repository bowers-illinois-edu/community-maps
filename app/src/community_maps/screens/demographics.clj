(ns community-maps.screens.demographics
  (:use shanks.core
        [community-maps tags util]
        clojure.contrib.strint)
  (:require
   [burp.forms :as bf]
   [hiccup.form-helpers :as f]))


(defscreen demographics
  [subject]

  (multiple-choice
   :on-your-mind
   "What were you thinking about as you were drawing your \"local community\"? Check all that apply:"
   {:weekly "People or places you see on a weekly basis"
    :people-like-you "People like you"
    :local-places "Your grocery store, library, post office, church, or other places you visit on a regular basis"
    :family "Family and friends"
    :voting "People or places you think about when you go vote in an election"
    :neighbourhood "Your neighbourhood"
    :newspapers "What you read about in newspapers"
    :tv "What you see on television or the internet"})
  (f/with-group :on-your-mind
    (bf/labeled-checkbox
     :other [:span "Other, please specify: " (f/text-field :other-explanation)]))

  (directions 
   "In order to make sense of our results and to make fair comparisons, we need to ask you to provide some more basic information about yourself.")

  (question
   "What is your highest level of educational attainment?"
   (bf/radio-group
    :edu
    {:some-secondary "Some Secondary (High School)"
     :hs-diploma "Secondary (High School) diploma"
     :some-college "Some college/university education"
     :college-diploma "College/University diploma"
     :graduate-education "Partial or complete graduate degree"}))

  (multiple-choice
   :race
   "Are you (You can mark more than one or specify, if applicable.):"
   (into {} [[:white "White"]
             [:southasian "South Asian (e.g., East Indian, Pakistani, Sri Lankan, etc.)"]
             [:chinese "Chinese"]
             [:black "Black"]
             [:filipino "Filipino"]
             [:latinam "Latin American"]
             [:arab "Arab"]
             [:southeastasian "Southeast Asian (e.g., Vietnamese, Cambodian, Malaysian, Laotian, etc.)"]
             [:westasian "West Asian (e.g., Iranian, Afghan, etc.)"]
             [:korean "Korean"]
             [:japanese "Japanese"]]))
  (f/with-group :race
    (bf/labeled-checkbox
     :other [:span "Other, please specify: " (f/text-field :other-explanation)])))

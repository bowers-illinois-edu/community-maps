(ns community-maps.screens.demographics
  (:use shanks.core
        [community-maps tags util]
        clojure.contrib.strint)
  (:require
    [burp.forms :as bf]))

(defscreen demographics
  [subject]
  (question
   "What is your highest level of educational attainment?"
   (bf/radio-group
    :edu
    {:some-secondary "Some Secondary (High School)"
     :hs-diploma "Secondary (High School) diploma"
     :some-college "Some college/university education"
     :college-diploma "College/University diploma"
     :graduate-education "Partial or complete graduate degree"}))
  (question
   "What is your yearly household income?"
   (bf/radio-group
    :income
    {:under20 "Less than $20,000"
     :20-40 "$20,000 to $39,999"
     :40-60 "$40,000 to $59,999"
     :60-100 "$60,000 to $99,999"
     :100-200 "$100,000 to $199,999"
     :more200 "More than $200,000"})))

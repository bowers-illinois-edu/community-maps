(ns community-maps.screens.demographics
  (:use shanks.core
        [community-maps tags util]
        clojure.contrib.strint)
  (:require
   [burp.forms :as bf]
   [hiccup.form-helpers :as f]))

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
     :more200 "More than $200,000"}))

  (question
   "The 2011 Canadian Census asks the following question in order to collect information in accordance with the Employment Equity Act and its Regulations and Guidelines to support programs that promote equal opportunity for everyone to share in the social, cultural, and economic life of Canada.<br/><br/>
You can mark more than one or specify, if applicable. Are you:"

   (bf/radio-group
    :race
    [[:white "White"]
     [:southasian "South Asian (e.g., East Indian, Pakistani, Sri Lankan, etc.)"]
     [:chinese "Chinese"]
     [:black "Black"]
     [:filipino "Filipino"]
     [:latinam "Latin American"]
     [:arab "Arab"]
     [:southeastasian "Southeast Asian (e.g., Vietnamese, Cambodian, Malaysian, Laotian, etc.)"]
     [:westasian "West Asian (e.g., Iranian, Afghan, etc.)"]
     [:korean "Korean"]
     [:japanese "Japanese"]
     [:other [:span  "Other. Please specify: " (f/text-field :other-description)]]])))

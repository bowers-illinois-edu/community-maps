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
    "In order to make better sense of our results and make comparisons, we would like to ask you to provide some more basic information about yourself.")

  (ordered-choice
   :sex
   "Are you male or female?"
   (keypairs
    :male "Male"
    :female "Female"))

  (ordered-choice
   :edu
   "What is your highest level of educational attainment?"
   (keypairs :no-schooling                               "no schooling"
              :some-primary                               "some elementary school"
              :completed-primary                          "completed elementary school"
              :some-secondary                             "some secondary / high school"
              :completed-secondary                        "completed secondary / high school"
              :some-technical                             "some technical, community college, CEGEP, College Classique"
              :completed-technical                        "completed technical, community college, CEGEP,College Classique"
              :some-university                            "some university"
              :completed-bachelor-degree                  "bachelor's degree"
              :completed-master-degree                    "master's degree"
              :completed-professional-degree-or-doctorate "professional degree or doctorate"))

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
     :other [:span "Other, please specify: " (f/text-field :other-explanation)]))

  (ordered-choice
   :news
   "During a typical week, how many days do you access news via TV, radio, the Internet, or printed newspaper?"
   (keypairs :zero  "0 (never access news)"
              :one   "1"
              :two   "2"
              :three "3"
              :four  "4"
              :five  "5"
              :six   "6"
              :seven "7 (access news everyday)"))

  (ordered-choice
   :polint
   "Using a scale from 1 to 10, where 1 means no interest at all and 10 means a great deal of interest, how interested are you in politics in general?"
   (keypairs :zero  "0 (no interest)"
              :one   "1"
              :two   "2"
              :three "3"
              :four  "4"
              :five  "5"
              :six   "6"
              :seven "7"
              :eight "8"
              :nine  "9"
              :ten   "10 (very interested)")))

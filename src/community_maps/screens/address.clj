(ns community-maps.screens.address
  (:use community-maps.tags
        shanks.core
        [burp.core :only [add-class]])
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]))

(defscreen address
  [subject]
  
;;;Q1.
  (question "In a moment, we will ask you to look at where you live on a map.
Please tell us your address. (Reminder: These surveys are anonymous, and your
address will never be used unless you explicitly give us permission to do so.)"
            (f/with-group :address-finder
              [:div.map-find-address
               [:div.map-canvas {:style "height: 400px; width: 100%;"}]
               (f/hidden-field {:class "latlng"} :latlng)
               (f/text-field {:class "address"} :address (:address subject))
               (add-class (button "Find on map") "update")])) ; if preloaded 


;;;Q2.	Question: 
  (question "How long have you lived in your current home?"
            (bf/radio-group :length-of-residence
                            {:less-than-one "Less than one year"
                             :x-years [:span (f/text-field :x-years) " years"]
                             :all-my-life "All my life"}))
;;;Q3.	Question: 

  (question
   "What city and province did you live in before moving to this home? Please select the best description and fill in the name of the location below."
   (bf/radio-group :other-residence
                   {:city "Elsewhere in the same city"
                    :province "Elsewhere in the same province"
                    :other "In another province"
                    :ex-canada "Outside Canada"})
   (f/text-field :other-residence-name))

;;;Q4.	Question:

  (single-choice
   :rent-own
   "Do you or your family own your own home/apartment, pay rent or what?"
   {:yes "We own"
    :rent "We rent"
    :other "We have another arrangement"})

  (case
   (:election-neighborhood subject)
   "national" (yes-no :national-election "Did you vote in the national election in May?")
   "provincial" (yes-no :provincial-election "Did you vote in the recent provincial election?")
   "like-live" (question
               "On the whole, do you like or dislike this neighborhood as a place to live. Would you say you like it a lot, like it, dislike it, dislike it a lot?"
               (bf/radio-group :like-neighborhood
                               {:like-alot "Like it a lot."
                                :like "Like it."
                                :dislike "Dislike it."
                                :dislike-alot "Dislike it a lot."}))
   "safety" (question
             "How worried are you about your safety in your neighborhood?  Are you very worried, somewhat worried, not very worried, or not at all worried?"
               (bf/radio-group :safe-neighborhood
                               {:worried "Worried"
                                :somewhat-worried "Somewhat worried"
                                :not-very-worried "Not very worried"
                                :not-at-all-worried "Not at all worried"}))
   "nothing" nil)

  (when (get #{"national" "provincial"} (:election-neighborhood subject))
    (question "For which party did you vote?"
              (f/text-field :national-provincial-party-choice))))

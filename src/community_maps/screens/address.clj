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
            [:div.map-find-address
             [:div.map-canvas {:style "height: 400px; width: 100%;"}]
             (f/text-field {:class "address"} :address (:address subject))
             (add-class (button "Find on map") "update")]) ; if preloaded 


;;;Q2.	Question: 
  (question "How long have you lived in your current home?"
            (bf/radio-group :length-of-residence
                            {:less-than-one "Less than one year"
                             :x-years (f/text-field :x-years)
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
    :other "We have another arrangement"}))

;;;Q5.	Question:
;;;Randomize:
;;;a)	Question about whether you voted in the recent provincial election. Follow – which party did you vote for.
;;;b)	2 questions re neighborhood:
;;;a.	On the whole, do you like or dislike this neighborhood as a place to live. Would you say you like it a lot, like it, dislike it, dislike ti a lot?
;;;b.	How worried are you about your safety in your neighborhood?  Are you very worried, somewhat worried, not very worried, or not at all worried?
;;;c)	 Question about whether you voted in the national election in May. Follow – which party did you vote for.
;;;d)	no prompt.

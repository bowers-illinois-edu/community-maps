(ns community-maps.screens.address
  (:use community-maps.tags
        shanks.core
        [burp.core :only [add-class]])
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]))

(defscreen address
  [subject]
  
;;;Q1.
  (question
   (list [:span.required "Required: "]
         "In a moment, we will ask you to look at where you live on a map.
Please enter your postal code in the box. (Reminder: Responses to this survey are confidential and will only be used for scientific research.)")
            (f/with-group :address-finder
              [:div.map-find-address
               (f/hidden-field {:class "latlng"} :latlng)
               (f/text-field {:class "address"} :address (:address subject))
               (add-class (button "Find on map") "update")
               [:div.map-canvas {:style "height: 400px; width: 100%;"}]
                 ])) ; if preloaded 


;;;Q2.	Question: 
  (question "How long have you lived in your current home?"
            (bf/radio-group :length-of-residence
                            {:less-than-one "Less than one year"
                             :x-years [:span (f/text-field :x-years) " years (please fill in the correct number)"]
                             :all-my-life "All my life"}))
;;;Q3.	Question: 

  (question
   "What city and province did you live in before moving to this home? Please select the best description and fill in the name of the location below."
   (bf/radio-group :other-residence
                   {:city "Elsewhere in the same city"
                    :province "Elsewhere in the same province"
                    :other "In another province"
                    :ex-canada "Outside Canada"})
   [:span (f/text-field :other-residence-name) " (please explain where)"])

;;;Q4.	Question:
  (question 
   "Do you or your family own your own home/apartment, pay rent or what?"
   (bf/radio-group
    :rent-own
    {:yes "We own"
     :rent "We rent"
     :other "We have another arrangement"})))

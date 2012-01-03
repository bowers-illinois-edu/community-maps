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
   "In a moment, we will ask you to look at where you live on a map.
Please enter your postal code in the box. If the map does not look right, please try entering the postal code (or an intersection or address) again. (Reminder: These surveys are confidential, and your postal code will not be used for any purpose aside from this survey.)"
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
                   (map (fn [[k [v1 v2]]] [k [:span v1 " " [:span.followup v2 " " (f/with-group k (f/text-field :details))]]])
                        {:city ["Elsewhere in the same city." "Please tell us the postal code if you recall it."]
                         :province ["Elsewhere in the same province." "Please tell us the name of the city or the postal code."]
                         :other ["In another province." "Please tell us the name of the city or the postal code."]
                         :ex-canada ["Outside Canada." "Please tell us the name of the country."]})))

;;;Q4.	Question:
  (question 
   "Do you or your family own your own home/apartment, pay rent or what?"
   (bf/radio-group
    :rent-own
    {:yes "We own"
     :rent "We rent"
     :other "We have another arrangement"}))

  (when (not (= "control" (subject :feel-close-to)))
    (list
     (question
      (str
       "Thinking about where you live, how close do you feel to "
       (when (not (= "Canada" (:feel-close-to subject))) "your ")
       (:feel-close-to subject) "?")
      (bf/radio-group
       :feel-close-to-district
       {:very-close "Very Close"
        :close "Close"
        :not-close "Not Close"
        :not-close-at-all "Not close at all"}))
     (question
      (str
       "If you could improve your work or living conditions, how willing or unwilling would you be to move to another "
       (if (= "Canada" (:feel-close-to subject)) "country"  (:feel-close-to subject)) "?")
      (bf/radio-group
       :willing-to-move
       {:very-willing "Very Willing"
        :fairly-willing "Fairly Willing"
        :neutral "Neither willing nor unwilling"
        :fairly-unwilling "Fairly unwilling"
        :very-unwilling "Very unwilling"})))))

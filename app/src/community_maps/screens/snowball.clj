(ns community-maps.screens.snowball
  (:use community-maps.tags
        shanks.core
        [burp.core :only [add-class]])
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]))

(defscreen snowball
  [subject]
  (directions
   (str "For these next questions, please think of someone with whom you discuss "
        (:snowball-prompt subject)
        ". In the following questions, we will call this person your \"conversation partner.\""))

  (question
   "Please show us on a map where you talk with your conversation partner. The location might be a home, a business, a public space, or anything else. You may enter an address or an intersection of streets."
   (f/with-group :discussion-location
              [:div.map-find-address
               [:div.map-canvas {:style "height: 400px; width: 100%;"}]
               (f/hidden-field {:class "latlng"} :latlng)
               (f/text-field {:class "address"} :address)
               (add-class (button "Find on map") "update")]))
  (question 
   (str  "How often do you discuss " (:snowball-prompt subject) " with your conversation partner?")
   (bf/radio-group :frequency {:fewer "Less often than one time per month"
                               1 "One time per month"
                               2 "Two times per month"
                               3 "Three times per month"
                               4 "Four times per month"
                               5 "Five times per month"
                               :more "More than 5 times per month"}))


  (single-choice :relationship
                 "How would you categorize your relationship with your conversation partner?"
                 {:family "Family"
                  :friends "Friends"
                  :coworkers "Coworkers"
                  :other "Other"})

  (single-choice :contact
                 "Would you be willing to allow us to email your conversation partner to request participation in this survey?"
                 {:no "No"
                  :yes [:span "Yes. My conversation partner's email address: " (f/text-field :partner-email)]}))

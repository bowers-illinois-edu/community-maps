(ns community-maps.screens.follow-up-survey
  (:use community-maps.tags
        shanks.core)
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]))

(defscreen follow-up-survey
  [subject]
  (doall
   (map
    #(vector :p %)
    ["Thank you for completing this survey. We very much value the time and effort you've spent. The responses you have provided will assist researchers in better understanding how Canadian society is changing and how those changes will affect communities such as yours and others across the country."

     "We want to remind you again that all of your responses will remain confidential and will only be used for academic research purposes."

     "Finally, we would like to invite you to complete a short, 5-7 minute, follow up survey in a few weeks. Everyone who completes the follow up will be entered in a drawing to win one of four iPad2 tablets."]))
  (question
   "If you agree to be contacted again for a brief 5-7 minute survey, please enter your email in this box."
   (f/text-field :email-address))) ; sub may
                                        ; have supplied it earlier,
                                        ; but rentering indicates
                                        ; interest

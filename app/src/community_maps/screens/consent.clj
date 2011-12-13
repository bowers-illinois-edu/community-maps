(ns community-maps.screens.consent
  (:use
   shanks.core
   [appengine-magic.core :only [resource-url]]
   [clojure.string :only (split)])
  (:require [burp.forms :as bf]))

;;; See app/war/consent.txt
(def consent-text 
  (split
   (slurp
    (resource-url "consent.txt"))
   #"\n\n"))

(defscreen consent
  [_]
  (map #(vector :p %) consent-text)
  (bf/labeled-checkbox
   :consent
   (str
    "I understand the general nature of this survey, "
    "I am 18 years of age or older, and I voluntarily agree to participate in this survey.")))


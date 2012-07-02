(ns community-maps.screens.vismin
  (:use shanks.core
        [community-maps tags util]
        clojure.contrib.strint)
  (:require
   [burp.forms :as bf]
   [hiccup.form-helpers :as f]))

(defscreen vismin
  [subject]
  (seven-point-scale :vmcanada 0 100 "What do you think the percentage of visible minorities in Canada is?")
  (seven-point-scale :vmcommunity 0 100 "What do you think the percentage of visible minorities in your community is?"))

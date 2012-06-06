(ns community-maps.previous-survey
  (:use clojure-csv.core)
  (:require [appengine-magic.core :as ae]))

;; the zoom, lat, and lon data from Survey 1
(def drawing-data
  (parse-csv
   (slurp
    (ae/resource-url "survey.one.redraw.csv"))))

;; index the drawing data by the previous ID for quick lookup
(def drawing-data-pid
  (reduce
   (fn [a i] (assoc a (first i) (rest i)))
   {}
   drawing-data))

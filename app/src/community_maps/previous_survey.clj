(ns community-maps.previous-survey
  (:use clojure-csv.core)
  (:require [appengine-magic.core :as ae]))

;; the zoom, lat, and lon data from Survey 1
(def drawing-data
  (parse-csv
   (slurp
    (ae/resource-url "survey.one.redraw.csv"))))

;; index the drawing data by the VCID for quick lookup
;; The columns are pid, vcid, old zoom, lat, lon, final zoom
(def drawing-data-vcid
  (reduce
   (fn [a i] (assoc a (second i) (cons (first i) (drop 2 i))))
   {}
   drawing-data))

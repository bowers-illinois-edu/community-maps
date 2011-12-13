(ns community-maps.util)

;;; Utility functions

(defn map-vals
  "Map a 'map' for the keys and return a new hash-map with the results of the function"
  [f m]
  (into {} (map (fn [[k v]] [k (f v)]) m)))

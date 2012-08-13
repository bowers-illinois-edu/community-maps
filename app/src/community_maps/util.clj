(ns community-maps.util)

;;; Utility functions

(defn map-vals
  "Map a 'map' for the keys and return a new hash-map with the results of the function"
  [f m]
  (into {} (map (fn [[k v]] [k (f v)]) m)))


(defn keypairs
  "Helper function to create a vector of vectors from list of arguments:
    :a 1 :b 2 => [[:a 1] [:b 2]]"
  [& pairs]
  (assert (even? (count pairs)))
  (partition 2 pairs))


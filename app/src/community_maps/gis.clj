(ns community-maps.gis
  (:require [clojure.string :as cstr]
            [appengine-magic.services.url-fetch :as url]))


;;; Some useful constants
(def *gisurl* "ec2-184-72-128-232.compute-1.amazonaws.com")

(def *districts* {"pr" "Province"
                  "ccs" "Census consolidated subdivision"
                  "cma" "Census metropolitan area"
                  "csd" "Census subdivision"
                  "ct" "Census tract"
                  "dpl" "Designated place"
                  "fed" "Federal electoral district"
                  "fsa" "Forward sortation area"
                  "cd" "Census division"
                  "ua" "Urban area"})


(defn get-subject-district-id
  "Look up the subject's district id, which can then be used to get a KML file"
  [subject district]
  (let [[lat lng] (cstr/split
                   (get-in subject [:address :address-finder :latlng])
                   #",")]
    (String. (:content
              (url/fetch
               (str "http://" *gisurl* "/district.php?"
                    "table=" district
                    "&lat=" lat
                    "&lon=" lng))))))

(defn kml-url
  "Get the KML url for a given district type and number"
  [type k]
  (str "http://" *gisurl* "/kml/" type "/" k ".kml"))


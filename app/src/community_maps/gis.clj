(ns community-maps.gis
  (:use [shanks.appengine-magic :only [dbsave]])
  (:require [clojure.string :as cstr]
            [appengine-magic.services.url-fetch :as url]))


;;; Some useful constants
(def *gisurl* "gis.mappingcommunities.ca")

(def *districts* {"pr" "province"
                  "ccs" "census consolidated subdivision"
                  "cma" "census metropolitan area"
                  "csd" "census subdivision"
                  "ct" "census tract"
                  "dpl" "designated place"
                  "fed" "federal electoral district"
                  "fsa" "forward sortation area"
                  "cd" "census division"
                  "ua" "urban area"
                  "da" "census dissemination area"})

;; text to insert when describing the regions
;; any level not included is not described
(def extended-descriptions
  {"cd" "which is a census unit"
   "csd" "which is a census unit"
   "da" "which is a census unit"
   "fsa" "which is a postal unit"
   })

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

(defn from-quebec?
  "Returns true if the subject is from Quebec, false otherwise. If the subject does not already have a :quebec key,
   this function calls the EC2 server to find the subject's 'pr' value and saves it to the db for future look ups.
   It would be a good idea to cache this return value within a screen to avoid multiple HTTP calls to EC2."
  [subject]
  (let [q (:quebec subject)]
    (if (nil? q)
      (let [pr (get-subject-district-id subject "pr")]
        (dbsave (assoc subject :quebec (= "24" pr)))
        (= "24" pr))
      q)))

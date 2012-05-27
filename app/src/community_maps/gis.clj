(ns community-maps.gis
  (:use [shanks.appengine-magic :only [dbsave]])
  (:require [clojure.string :as cstr]
            [appengine-magic.services.url-fetch :as url]))


;;; Some useful constants
(def *gisurl* "gis.mappingcommunities.ca")

(def *districts* {"pr" "province"
                  "ccs" "ubdivision de recensement unifiée"
                  "cma" "région métropolitaine de recensement"
                  "csd" "subdivision de recensement"
                  "ct" "secteur de recensement"
                  "dpl" "endroit désigné"
                  "fed" "circonscription électorale fédérale"
                  "fsa" "région de tri d'acheminement"
                  "cd" "division de recensement"
                  "ua" "région urbaine"
                  "da" "aire de diffusion de recensement"})

;; text to insert when describing the regions
;; any level not included is not described
(def extended-descriptions
  {"cd" "qui est une unité du recensement"
   "csd" "qui est une unité du recensement"
   "da" "qui est une unité du recensement"
   "fsa" "qui est une unité postal"
   })

(defn get-subject-district-id
  "Look up the subject's district id, which can then be used to get a KML file"
  [subject district]
  (let [[lat lng] (cstr/split
                   (get subject :address-address-finder-latlng)
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

(defn get-province 
  "Fetches the province code (not the name) from the EC2 gis server. Saves it under :province in the db."
  [subject]
  (let [p (:province subject)]
    (if (nil? p)
      (let [pr (get-subject-district-id subject "pr")]
        (dbsave (assoc subject :province pr))
         pr)
      p)))

(defn from-quebec?
  "Returns true if the subject is from Quebec, false otherwise. If the subject does not already have a :quebec key,
   this function calls the EC2 server to find the subject's 'pr' value and saves it to the db for future look ups.
   It would be a good idea to cache this return value within a screen to avoid multiple HTTP calls to EC2."
  [subject]
  (= "24" (get-province subject)))

(defn from-alberta?
  "Returns true if the subject is from Quebec, false otherwise. If the subject does not already have a :quebec key,
   this function calls the EC2 server to find the subject's 'pr' value and saves it to the db for future look ups.
   It would be a good idea to cache this return value within a screen to avoid multiple HTTP calls to EC2."
  [subject]
  (= "48" (get-province subject)))


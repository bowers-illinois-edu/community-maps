(ns community-maps.translate
  (:use [community-maps.output :only [escape-str-for-csv]]))

(def files
  ["core.clj"
   "gis.clj"
   "mail.clj"
   "tags.clj"
   "screens/address.clj"
   "screens/draw.clj"
   "screens/everything.clj"
   "screens/follow_up_survey.clj"
   "screens/minorities_community.clj"
   "screens/own_community.clj"])

(defn file->strs
  "For a given file name w/o src/community_maps prefex, reduce to a list of strings"
  [file]
  (filter
   string?
   (flatten
    (read-string
     (str "("
          (slurp (str "src/community_maps/" file))
          ")")))))

(defn make-csv
  "Make up a CSV that lists filename and string in two columns"
  [file]
  (let [file-name-column (escape-str-for-csv file)]
    (map
     (fn [s]
       [file-name-column (escape-str-for-csv s)])
     (file->strs file))))

(defn all-files-csv
  []
  (reduce
   into
   (map make-csv files)))

(defn csv-text
  "Turn the internal representation into a bunch of print statements"
  []
  (binding [*out* (java.io.FileWriter. "strings.csv")]
    (println "File,String")
    (doall (map #(print (first %) "," (second %) "\n") (all-files-csv)))
    nil))

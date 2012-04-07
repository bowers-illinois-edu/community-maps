(ns community-maps.upgrade-subjects
  (:use shanks.appengine-magic)
  (:import com.google.appengine.api.datastore.Entity
           com.google.appengine.api.datastore.PreparedQuery
           com.google.appengine.api.datastore.Query
           shanks.appengine_magic.Subject
           java.util.Date)
  (:require [appengine-magic.services.datastore :as ds]
            [appengine-magic.services.task-queues :as tq]))

;;; Load routines from previous version of shanks
(ds/defentity SubMap [^:clj name-in-parent])

(defn- restore-maps-vecs
  "Restore the items that were stored in SubMap and SubVec objects"
  [root submaps]
  (let [rk (ds/get-key-object root)
        direct-maps (filter #(= rk (.getParent (ds/get-key-object %))) submaps)
        recursed-maps (map #(restore-maps-vecs % submaps) direct-maps)
        Text-keys (filter #(= com.google.appengine.api.datastore.Text (class (get root %))) (keys root))]
    (into
     (into root (map #(vector % (.getValue (get root %))) Text-keys))
     (map (fn [x] [(:name-in-parent x) (dissoc x :name-in-parent)]) recursed-maps))))

(defn- attach-subdata
  "Given a Subject object, load and attach all subdata"
  [root]
  (restore-maps-vecs
   (assoc root :id (ds/key-id root)) ; the rest of the system
                                        ; assumes a :id tag, this will
                                        ; get filtered out on save
   (ds/query :kind SubMap :ancestor root)))

(defn- old-dbload
  "Load a Subject by ID number"
  [id]
  (if (or (nil? id) (= 0 id))
    nil
    (let [root (ds/retrieve Subject id)]
      (when root 
        (attach-subdata root)))))

;;; need to query on schema-version-number to see who is < 2

(defn get-old-subjects
  "Fetch a specified number of old subjects"
  [n]
  (let [subs (ds/query :kind shanks.appengine_magic.Subject)]
    (take n (filter #(nil? (:schema-version-number %)) subs))))


;;; a web hook to upgrade the subjects
(defn upgrade-old-subjects
  [_]
  (let [olds (get-old-subjects 10)]
    (doall (map dbsave (map attach-subdata olds)))
    {:status 200 :headers {"ContentType" "text/plain"}
     :body (str "Processed " (count olds) " old subjects.")}))

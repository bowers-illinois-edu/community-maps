(ns community-maps.mail
  (:use compojure.core
        ring.middleware.params)
  (:require [appengine-magic.services.mail :as m]))

(def *from* "admin@mappingcommunities.ca")

(defn mail-resume-link
  [id email]
  (let [body (str
              "Hello,\n\nThank you for taking the MappingCommunities.ca survey. You can resume where you left off by visiting the following link:\n\n"
              "http://www.mappingcommunities.ca/?id=" id
              "\n\nThank you,\nMappingCommunities Team\n")]
    (m/send
     (m/make-message
      :from *from*
      :to email
      :subject "MappingCommunities Survey"
      :text-body body))
    {:status 200 :headers {"Content-Type" "text/plain"} :body (str "Message sent to " email "\n" body) }))

(defn add-mail-urls
  [app]
  (wrap-params (routes
                (GET "/mail/resume" [id email] (mail-resume-link id email))
                app)))

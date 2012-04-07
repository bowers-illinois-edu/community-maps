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

(defn- extract-comment-email
  "See if the user has left a comment at a specified step"
  [subject step]
  {:comment (get subject (keyword (str "comments-" step)))
   :email (if (not (= "" (get subject :email-address ""))) (:email-address subject) false)})

(defn mail-comments
  "Checks to see if there is data for in the comment field for the current step of the subject"
  [subject]
  ;; assume that the step counter has been incremented already, so we
  ;; need the previous value
  (let [step (dec (get subject :step 0))
        extracted (extract-comment-email subject step)]
    (when (and
           (not (nil? (:comment extracted)))
           (not (= (:comment extracted) "")))
      (m/send
       (m/make-message
        :from *from*
        :to "admin@mappingcommunities.ca"
        :subject (str "A comment" (when  (:email extracted) (str " from " (:email extracted))))
        :text-body
        (str "While on page " step " "
             (if (:email extracted) (:email extracted) "a respondent")
             " left the following comment:\n\n"
             (:comment extracted)
             "\n\n##########"))))))


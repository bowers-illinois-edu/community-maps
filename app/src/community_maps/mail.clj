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

(defn mail-comments
  "Checks to see if there is data for in the comment field for the current step of the subject"
  [subject]
  ;; assume that the step counter has been incremented already, so we
  ;; need the previous value
  (let [step (dec (get subject :step 0))
        comment (get subject (keyword (str "comments-" step)))
        included-email (not (= "" (get subject :email "")))
        email (if included-email (:email subject) *from*)]
    (when (and
           (not (nil? comment))
           (not (= comment "")))
      (m/send
       (m/make-message
        :from *from*
        :reply-to (if included-email email *from*)
        :to "admin@mappingcommunities.ca"
        :subject (str "A comment" (when included-email (str " " (:email subject))))
        :text-body
        (str "While on page " (dec (:step subject)) " "
             (if included-email (:email subject) "a respondent")
             " left the following comment:\n\n"
             comment
             "\n\n##########"))))))


(ns community-maps.mail
  (:use [appengine-magic.core :only [appengine-base-url]]
        [ring.middleware.params :only [wrap-params]]
        compojure.core)
  (:require [appengine-magic.services.mail :as mail]))

(def admin-email "admin@ourdomain.ca")

(defn resume-link
  "Send the user a link that he/she can use to resume survey later"
  [id email]
  (mail/send
   (mail/make-message
    :from admin-email
    :to email
    :subject "Continue the Community Mapping survey"
    :text-body (str "Dear Friend,

Thank you for starting the Community Mapping survey. We are excited to see how you see view your community. To pick up where you left off, please visit the following link:\n"
                    appengine-base-url "?id=" id "\n"
                    "Thank you again for your participation,\nThe Community Mapping Survey Team\n"))))

(defn wrap-resume-link
  "Adds a handler to the app for the resume link at /resume?id=x&email=y"
  [app]
  (wrap-params
   (routes
    (GET "/resume" req
         (do (resume-link (get (:params req) "id") (get (:params req) "email"))
             (str "Ok: " (get (:params req) "email") " -- " (get (:params req) "id"))))
    app)))

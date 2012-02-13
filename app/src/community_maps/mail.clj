(ns community-maps.mail
  (:use compojure.core)
  (:require [appengine-magic.services.mail :as m])) 

(defn mail-test
  [_]
  (m/send
   (m/make-message
    :from "mark.m.fredrickson@gmail.com"
    :to "mark.m.fredrickson@gmail.com"
    :subject "This is a test"
    :text-body "This is the body of the message. Yay."))
  {:status 200 :headers {"Content-Type" "text/plain"} :body "Test sent" })

(defn add-mail-urls
  [app]
  (routes
   ;(GET "/mail/resume" [params :params] resume)
   (GET "/mail/test" [] mail-test)
   app))

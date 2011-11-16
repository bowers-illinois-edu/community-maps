(ns community-maps.app_servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use community-maps.core)
  (:use [appengine-magic.servlet :only [make-servlet-service-method]]))


(defn -service [this request response]
  ((make-servlet-service-method community-maps-app) this request response))

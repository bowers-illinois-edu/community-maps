(ns community-maps.core
  (:use [shanks core appengine-magic]
        hiccup.core
        hiccup.page-helpers
        [burp.ring :only [wrap-burp]]
        [burp.jquery :only [jquery-link jquery-ui-link]]
        ring.middleware.file
        [community-maps.screens address draw everything own-community minorities-community follow-up-survey]
        [clojure.string :only [split]]
        community-maps.output
        [community-maps.mail :only [add-mail-urls mail-comments]]
        compojure.core
        [community-maps.tags :only [directions]]
        [community-maps.upgrade-subjects :only [upgrade-old-subjects]]
        clojure.contrib.strint)
  (:require [appengine-magic.core :as ae]
            [appengine-magic.services.datastore :as ds]
            [burp.forms :as bf]
            [hiccup.form-helpers :as f]
            [community-maps.gis :as gis]))


(defn randomizer []
  (randomize-subject
   {:feel-close-to ["contrôle" "quartier" "ville" "province" "Canada"]
    :get-welfare ["minorités ethniques" "gens"]
    :minority-projection ["aucun" "25" "35" "50"]
    :minority-population-share ["augmentation" "diminution"]
    :ethnic-shop ["minorités ethniques" "autres membres avec les mêmes origines ethniques"]
    :outgroup-marry ["race" "ethnic background"]
    :display-district ["pr" "csd" "canada" "fsa" "fed" "da"]
    :drawing-zoom [10 12 14 16 17]
    ; the next randomization is kind of a hack to get 1/10 subjects
    ; assigned to the "on your mind question" (as the system draws
    ; with eq prob from the options
    :on-your-mind-question (conj (repeat 9 false) true)
    ; the next randomizations for 3 questions on the racial-conflict
    ; screen (of the same name)
    :taxes-increased ["votre communauté locale" "votre ville" " votre province"]
    :french-language ["Votre gouvernement municipal" "Votre gouvernement provincial" "Le gouvernement canadien"]
    :anti-racism-unit ["votre communauté locale" "votre ville" "Le gouvernement canadien" "Canada"]
    :anti-racism-cirriculum ["dans les études autochtones et les mouvements antiracistes" "mettre l'accent sur les origines européennes de l'histoire et des traditions canadiennes."]
    }))

(defn createwithid [req]
  (let [key (dbsave (-> (randomizer)
                        (assoc :vcid (get-in req [:params :vcid]))
                        (assoc :tags (get-in req [:params :tags]))))]
    (dbload (ds/key-id key))))

(defmulti layout (fn [subject screen] screen))

(def css (list
          (include-css "http://yui.yahooapis.com/2.7.0/build/reset-fonts-grids/reset-fonts-grids.css")
          (include-css "http://yui.yahooapis.com/2.8.2r1/build/base/base-min.css")
          (include-css "https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/themes/ui-lightness/jquery-ui.css")
          (include-css "local.css")))

(defn body [header body]
  [:body {:id "doc" :class "yui-t7"}
    [:div#hd header]
    [:div#bd
     [:div.yui-g body]]])
  
(defn screen-form-button
  "Wrap a screen in a form with a funky button"
  [scrn subject]
  (f/form-to [:post "/"]
             (f/hidden-field :id (:id subject))
             (scrn subject)
             [:h4.comments-label (f/label (str "comments-" (:step subject)) "Questions ou commentaires? Cliquez ici.")]
             [:div.comments-subform
              [:p "Si vous avez des questions ou des commentaires, n'hésitez pas à les insérer dans la boîte ci-dessous. Les commentaires seront enregistrés lorsque vous cliquerez sur le bouton \"continuez\". Si vous désirez que l'on communique avec vous à propos de vos questions ou commentaires, nous vous prions d'inclure votre adresse courriel ci-dessous. "]
              (f/text-area (str "comments-" (:step subject)))
              [:div.email (f/label :email-address "Courriel: ")
               (f/text-field :email-address (:email-address subject))]]
             (f/submit-button {:class "continue fg-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"} "Continuez")))


;;; The consent and thank you pages have special layout functions,
;;; which makes writing them out a little more verbose (i.e. two
;;; functions), but it works best with the backend infrastructure this
;;; way

(defn load-and-process-txt
  "Load a text file and turn it into basic HTML. Returns a vector of [[:p \"paragraph 1\"] ...]"
  [filename]
  (map #(vector :p %)
       (split
        (slurp
         (ae/resource-url filename))
        #"\n\n")))

;; load this text at compile time to save processing
(def consent-text (load-and-process-txt "consent.txt"))
(defscreen consent
  [_]
  consent-text
  (bf/labeled-checkbox
   :consent
   (str
    "Les enquêtes scientifiques exigent un consentement formel des participants. Si vous désirez participer, cochez la case suivante. ")))

(defn screen-form-simple
  "Wrap a screen in a form with a funky button"
  [scrn subject]
  (f/form-to [:post "/"]
             (f/hidden-field :id (:id subject))
             (scrn subject)
             (f/submit-button {:class "continue fg-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"} "Continue")))

(defmethod layout consent [subject screen]
  (xhtml
   [:head
    [:title "Bienvenue"]
    (jquery-link)
    (jquery-ui-link)
    (include-js "burp.jquery.ui.support.js")
    (include-js "questions.js")
    css]
   (body
    (list
     [:img#rulogo {:src "RU_logo.gif" :alt "Ryerson Univeristy Logo"}]
     [:br {:style "clear:both"}]
     [:h1 "Bienvenue"])
    (screen-form-simple screen subject))))

(def thank-text (load-and-process-txt "thank.txt"))

(defscreen thank-you [_] thank-text)

(defmethod layout thank-you [subject screen]
  (xhtml
   [:head
    [:title "Merci"]
    css]
   (body
    (list
     [:img#rulogo {:src "RU_logo.gif" :alt "Ryerson University Logo"}]
     [:br {:style "clear:both"}]
     [:h1 "Merci"])
    (screen subject))))

(def screens [consent
              address
              draw
              own-community
              randomized-district
              racial-ethnic
              minorities-community
              racial-conflict
              follow-up-survey
              thank-you])

(defmethod layout :default [subject screen]
  (xhtml
   [:head
    [:title "Enquête visant à cartographier les communautés"]
    (jquery-link) (jquery-ui-link)
    (include-js "burp.jquery.ui.support.js")
    (include-js "address.js")
    (include-js "map_main.js")
    (include-js "scribble.js")
    (include-js "utilities.js")
    (include-js "questions.js")
    (include-js "resume.js")
    (include-js "http://maps.google.com/maps/api/js?v=3.4&sensor=false")
    css]
   [:body {:id "doc" :class "yui-t7"}
    [:div#hd
     [:a#resume {:class "fg-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"}
      [:span.ui-button-text "Reprendre plus tard"]]
     [:div#resume-popup
      [:p "Vous pouvez reprendre plus tard là où vous êtes. Il suffit de fournir votre adresse courriel et nous vous enverrons un lien qui vous permettra de reprendre sans perdre ce que vous avez déjà complété."]
      [:input.email]]
     [:h1 (<< "Enquête visant à cartographier les communautés (Page ~{(get subject :step 1)} of ~{(- (count screens) 2)})")]]
    [:div#bd
     [:div.yui-g (screen-form-button screen subject)]]]))

;;; add an extra route for the comments/ url
(defn add-data-urls
  [app]
  (routes
   (GET "/data/cron" [] build-data-cron)
   (GET "/data/dump-data" [] build-data-dump)
   (GET "/data/data.yaml" [] data-yaml-file)
   (GET "/data/comments" [] comments-page)
   (GET "/data/upgrade-subjects" [] upgrade-old-subjects)
   app
   (GET "*" [] (fn [_] {:status 404 :headers {"ContentType" "text/plain"} :body "Pas trouvée"}))))



;;; this is the app as called by the appengine-magic library
(def survey-app
  (-> (survey createwithid #(do (mail-comments %) (dbsave %)) dbload layout 
              screens)
      wrap-burp
      add-data-urls
      add-mail-urls))

(ae/def-appengine-app community-maps-app #'survey-app)



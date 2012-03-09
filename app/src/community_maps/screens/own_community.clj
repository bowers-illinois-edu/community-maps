(ns community-maps.screens.own-community
  (:use community-maps.tags
        shanks.core)
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]))

(defscreen own-community
  [subject]

  (directions "Now, for these next few questions we’ll be referring to the
area you highlighted.")
  (static-map-communities subject)
  (when (:on-your-mind-question subject)
    (list
     (multiple-choice
      :on-your-mind
      "What were you thinking about as you were drawing your \"local community\"? Check all that apply:"
      {:weekly "People or places you see on a weekly basis"
       :people-like-you "People like you"
       :local-places "Your grocery store, library, post office, church, or other places you visit on a regular basis"
       :family "Family and friends"
       :voting "People or places you think about when you go vote in an election"
       :neighbourhood "Your neighbourhood"
       :newspapers "What you read about in newspapers"
       :tv "What you see on television or the internet"})
     (f/with-group :on-your-mind
       (bf/labeled-checkbox
        :other [:span "Other, please specify: " (f/text-field :other-explanation)]))))
;;;Q6.  Question
  (group-sliders
   subject
   :community-percentage
   "Just your best guess – what percentage of the population of this community is:")

;;;Q7.  Question:
  (learn-about-composition
   :community-composition
   "How did you learn about the composition of your local community?")

  (directions "Here are some statements about the people in your local community. Please tell us how strongly you agree or disagree with each of these statements.")

;;;Q9, Q10, Q11

  (doall
   (map
    (fn [[id prompt]] (question prompt (agree-disagree id)))
    {:help "People around here are willing to help others in their community."
     :get-along "People in this community generally don’t get along with each other."
     :share-values "People in this community do not share the same values."}))

  (directions "For each of the following, please tell us if it is very likely, likely, unlikely or very unlikely that people in your local community would act in the following manner.")

;;;Q12., Q13.
  (doall
   (map
    (fn [[id prompt]] (question prompt (likelihood id)))
    {:graffiti "If some children were painting graffiti on a local building or house, how likely is it that people in your community would do something about it?"
     :community-organize "Suppose that because of budget cuts the library closest to your home was going to be closed down by the city. How likely is it that community residents would organize to try to do something to keep the library open?"}))

;;;Participation Questions
  (directions "Now we would like to know whether you have been involved in your community recently.")

  (doall
   (map
    (fn [[id prompt]] (question prompt (yes-no-dk id)))
    {:informalpartic "During the past 12 months, have you worked with other people to deal with some issue facing your community or schools?"
     :meetingpartic "During the past twelve months, did you attend a meeting about an issue facing your community or schools?"}
    )))

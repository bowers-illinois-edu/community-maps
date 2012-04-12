(ns community-maps.screens.everything
  (:use community-maps.tags
        shanks.core
        [burp.core :only [add-class]]
        clojure.contrib.strint)
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]
            [community-maps.gis :as gis]))

(defscreen randomized-district
  [subject]
  (list
  ;;; District Map Related Questions (if we can't look up the
  ;;; district, they are skipped entirely.
   (let [dst (:display-district subject)
         dst-name (get gis/*districts* dst)
         district-id (gis/get-subject-district-id subject dst)
         prompt (if (= "canada" dst) "Canada" (str "your " dst-name))]
     (when (and (not (= 0 district-id)) (not (= "" district-id)))
       (list
        (list
         (directions
          (<< "Please look at this map. The highlighted area shows ~{prompt}~{(when (get gis/extended-descriptions dst) (str \", \" (gis/extended-descriptions dst)))}.")
          (<< "Referring to this map of ~{prompt}, we would like to ask a series of questions just like the previous ones:"))
         (if (= "canada" dst)
           [:img {:src "/canada.jpg"}]
           (kml-map (gis/kml-url dst district-id))))

      ;;;Q14.   Question:
        (group-sliders
         subject
         :census-community
         (<< "Just your best guess - what percentage of the population in ~{prompt} is:"))

      ;;;Q15.   Question:
        (learn-about-composition
         :census-composition
         (<< "How did you learn about the composition of ~{prompt}?"))

      ;;;Q16.   Question:
        (question
         (<< "On the whole, do you like or dislike ~{prompt} as a place to live?")
         (bf/radio-group
          :like-dislike-census
          {:like-alot "Like it a lot"
           :like "Like it"
           :dislike "Dislike it"
           :dislike-alot "Dislike it a lot"}))

      ;;;Q17.   Question:
        (yes-no :census-feel-community
                (<< "On the whole, do you think that people who live in ~{prompt} feel a sense of community?"))

        (question
         (<< "In the last 5 years, do you think ~{prompt} has become more racially and ethnically diverse, less racially and ethnically diverse, or has remained about the same?")
         (bf/radio-group
          :perceived-diversity-chang
          {:more "More diverse"
           :less "Less diverse"
           :same "Remained about the same"}))
;;;Q18. Question:
        (question
         (<< "Some political leaders argue that in the next 10 years, racial and ethnic minorities will ~{(:minority-population-share subject)} their share of the population in ~{prompt} by a lot. Do you think such a change would be a good or bad thing if it happened?")
         (bf/radio-group :ethnic-growth {:good "Good thing" :neutral "Neither Good nor Bad" :bad "Bad thing"}))


;;;What is the largest nonwhite group?

        (directions
         "Now thinking more generally about Canada as a whole, we would like you to tell us if you feel particularly close to people in the following groups, if you feel the people in the groups are like you in their ideas and interests and feelings about things.")
;;;
;;;Q21. Question:
;;;
        (let [mc (multiple-choice
                  :close-to-group
                  "Please click on all of the groups to which you feel close."
                  (merge
                   (dissoc (ethnic-political-groups subject) :other-asian)
                   {:local-community "People in your local community"
                    :census-community (<< "People in ~{prompt}")}))]
          (assoc-in mc [2 1]
                    (concat
                     (second (first (get-in mc [2 1])))
                     [(f/with-group "close-to-group" (bf/labeled-checkbox "other-asian" "Other Asians (Korean, Japanese, Filipino, etc.)"))]))))))

   (question
    "When it comes to social and political matters, some people think of themselves mainly as White, Chinese, or Black and that is very important to how they think of themselves. Other people don’t tend to think of themselves in these ways. When it comes to social and political matters, how important is your race or ethnicity to how you think of yourself?"
    (bf/radio-group
     :has-ethnic-identity
     {:very-important "Very important"
      :somewhat-important "Somewhat important"
      :not-very-important "Not very important"
      :not-important "Not important at all"}))

   ;; Party ID and vote choice questions
   (question 
    "In federal politics, do you usually think of yourself as a:"
    (bf/radio-group
     :party-id
     (conj (shuffle (vec (political-groups-supporter subject))) [:none "None of these"])))

   [:div.election-choice.national-election-choice
    (add-class
     (yes-no :national-election "Did you vote in the federal national election in May, 2011?")
     :did-vote)
    (add-class
     (question
      "For which party did you vote?"
      (bf/radio-group :national-election-choice
                      (conj
                       (shuffle (vec (political-groups subject)))
                       [:other "Another party"])))
     :vote-choice)]

   [:div.election-choice.provincial-election-choice
    (add-class
     (yes-no :provincial-election "Did you vote in the most recent provincial election?")
     :did-vote)
    (add-class
     (question
      "For which party did you vote?"
      (bf/radio-group :provincial-election-choice
                      (conj
                       (shuffle (vec (political-groups subject)))
                       [:other "Another party"])))
     :vote-choice)]

   (when (gis/from-alberta? subject)
     (vector :div.election-choice.alberta-election
      (add-class
       (question
        "So far as you know now, do you expect to vote in the provincial election on April 23?"
        (bf/radio-group :alberta-vote-0423
                        {:yes "Yes, expect to vote"
                         :no "No, do not expect to vote"}))
       :did-vote)
      (add-class
       (question "For which party did you vote?"
                 (bf/radio-group :alberta-vote-0423-choice
                                 (conj
                                  (shuffle 
                                   [[:liberal "Liberal Party"]
                                    [:conservative "Progressive Conservative Party"]
                                    [:wildrose "Wildrose"]
                                    [:ndp "New Democratic Party (NDP)"] 
                                    [:alberta "Alberta Party"]])
                                  [:other "Another party"])))
       :vote-choice)))
   
   (question
    [:div
     (when-not (= "none" (:minority-projection subject))
       [:p
        "According to projections by the census, the number of visible minorities is going to reach "
        (:minority-projection subject)
        "% of the Canadian population in the next 10 years, largely as a result of immigration."])
     [:p "Do you think the number of immigrants from foreign countries who are permitted to come to Canada to live should be increased a little, increased a lot, decreased a little, decreased a lot, or left the same as it is now?"]]
    (bf/radio-group
     :increase-immigration
     {:increase-much "Increased a lot"
      :increase-little "Increased a little"
      :same "Left the same as it is now"
      :decrease-little "Decreased a little"
      :decrease-much "Decreased a lot"})))

  (question
   "Do you agree or disagree with the following statement? Speaking English or French should be a requirement for immigration to Canada."
   (agree-disagree :req-lang-immigration))
  no-back-button-msg)


(defscreen racial-ethnic [subject]
;;;Q29. Question:


  (directions
   "Please read the following statements and for each one, tell us how strongly you agree or disagree.")
;;;
;;;Q 30, 31. Q32, Q33, Q34, Q35
  (doall
   (map
    (fn [[k p]] (question p (agree-disagree k)))
    {:melting-pot
     "It is better for Canada if different racial and ethnic groups maintain their distinct cultures in a cultural mosaic rather than blend together."

     :happier-with-other-groups
     "People are generally happier when they live and socialize with others of different racial and ethnic backgrounds."

     :special-favors
     "Irish, Italian, Jewish and many other minorities overcame prejudice and worked their way up. Other minorities should do the same without any special favors."

     :try-harder
     "It's really a matter of some people not trying hard enough; if racial and ethnic minorities would only try harder they could be just as well off as whites."

     :gov-attention
     "Government officials usually pay less attention to a request or complaint from someone who is a racial or ethnic minority than from someone who is white."}))

;;;Q36. Question:
  (question
   (<< "How would it make you feel if a close relative of yours were planning to marry a person of a different ~{(:outgroup-marry subject)} from yours?")
   (bf/radio-group :marry-ethnic
                   {:very-uneasy "Very uneasy"
                    :somewhat-uneasy "Somewhat uneasy"
                    :not-uneasy "Not uneasy at all"}))

;;;Q37. Question:
  (question
   "How would it make you feel if a close relative of yours were planning to marry a person who had strong political beliefs different from your own?"
   (bf/radio-group :marry-political
                   {:very-uneasy "Very uneasy"
                    :somewhat-uneasy "Somewhat uneasy"
                    :not-uneasy "Not uneasy at all"}))
  no-back-button-msg)

(defscreen racial-conflict [subject]
  (question
   "On the whole, do you like or dislike your neighbourhood as a place to live."
   (bf/radio-group :like-neighbourhood
                   {:like-alot "Like it a lot"
                    :like "Like it"
                    :dislike "Dislike it"
                    :dislike-alot "Dislike it a lot"}))
  (question
   "How worried are you about your safety in your neighbourhood?"
   (bf/radio-group :safe-neighbourhood
                   {:worried "Worried"
                    :somewhat-worried "Somewhat worried"
                    :not-very-worried "Not very worried"
                    :not-at-all-worried "Not at all worried"}))

  (question
   "If you could find housing that you liked, would you rather live with neighbours who mostly share your political beliefs and values, or who hold a wide range of political beliefs and values, or is it not important to you?"
   (bf/radio-group
    :housing-political
    {:share "Mostly share my political beliefs and values"
     :diversity "Mostly hold a wide range of political beliefs and values"
     :not-important "This is not important to me"}))

  (question
   "What about when it comes to the race and ethnicity of your neighbours? If you could find housing that you liked, would you rather live with neighbours who share your racial and ethnic background, or who represent a mix of racial and ethnic backgrounds, or is it not important to you?"
   (bf/radio-group
    :housing-ethnic
    {:same "Mostly share my racial and ethnic background"
     :diversity "Mostly a mix of racial and ethnic backgrounds"
     :not-important "This is not important to me"}))

  (directions
   "Please read the following statements and for each one, tell us how strongly you agree or disagree.")

  (doall
   (map
    (fn [[key prompt]] (question prompt (agree-disagree key)))
    {:taxes-increased
     (<< "Taxes should be increased in ~{(:taxes-increased subject)} to help improve public transport and roads.")

     :french-language
     (<< "~{(:french-language subject)} should treat French like any other minority language.")

     :anti-racism
     (<< "Schools in ~{(:anti-racism-unit subject)} should be required to have initiatives ~{(:anti-racism-cirriculum subject)}.")}))
  
  (single-choice {:id "employed-student"} :employed
                 "Are you currently employed or enrolled as a student?"
                 {:employed "I am employed"
                  :student "I am a student"
                  :retired "I am retired"
                  :unemployed "I am not employed"})
  [:div#employment-follow-up
   (question
    "Would you please tell us where you work or study? Please provide the postal code, or if you don't know the postal code, please provide an intersection or address."
    (f/text-field :work-study-address))

;;;Q20. Question:
   (question
    "Are the people at your work (or school) mostly white, mostly racial or ethnic minorities, about half and half, or some other mixture?"
    (bf/radio-group :work-ethnicity
                    {:white "Mostly white"
                     :ethnic [:span "Mostly racial or ethnic minorities. Please explain: " (f/text-field :ethnic-description)]
                     :half "About half and half"
                     :other [:span "Some other mixture. Please explain:" (f/text-field :other-description)]}))]
  no-back-button-msg)

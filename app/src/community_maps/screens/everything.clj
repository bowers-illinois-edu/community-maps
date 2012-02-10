(ns community-maps.screens.everything
  (:use community-maps.tags
        shanks.core
        [burp.core :only [add-class]])
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
               (str "Please look at this map. The highlighted area shows " prompt ".")
               (str "Referring to this map with " prompt
                    " it, we would like to ask a series of questions just like the previous ones:"))
         (if (= "canada" dst)
           [:img {:src "/canada.jpg"}]
           (kml-map (gis/kml-url dst district-id))))
        
      ;;;Q14.	Question:
        (group-sliders
         subject
         :census-community
         (str "Just your best guess - what percentage of the population in "
              prompt
              " is:"))

      ;;;Q15.	Question:
        (learn-about-composition
         :census-composition
         (str "How did you learn about the composition of " prompt "?"))
        
      ;;;Q16.	Question:
        (question
         (str "On the whole, do you like or dislike "
              prompt
              " as a place to live?")
         (bf/radio-group
          :like-dislike-census
          {:like-alot "Like it a lot"
           :like "Like it"
           :dislike "Dislike it"
           :dislike-alot "Dislike it a lot"}))

      ;;;Q17.	Question:
        (yes-no :census-feel-community 
                (str "On the whole, do you think that people who live in "
                     prompt
                     " feel a sense of community?"))

;;;Q18.	Question:
        (question
         (str "Some political leaders argue that in the next 10 years, racial and ethnic minorities will "
              (:minority-population-share subject)
              " their share of the population in "
              prompt
              " by a lot. "
              "Do you think such a change would be a good or bad thing if it happened?")
         (bf/radio-group :ethnic-growth {:good "Good thing" :neutral "Neither Good nor Bad" :bad "Bad thing"}))


;;;What is the largest nonwhite group?

        (directions
         "We would like you to tell us if you feel particularly close to people in the following groups, if you feel the people in the groups are like you in their ideas and interests and feelings about things.")
;;; 
;;;Q21.	Question:
;;; 
        (let [mc (multiple-choice
                  :close-to-group
                  "Please click on all of the groups to which you feel close."
                  (merge
                   (dissoc (ethnic-political-groups subject) :other-asian)
                   {:local-community "People in your local community"
                    :census-community (str "People in " prompt)}))]
          (assoc-in mc [2 1]
                    (concat
                     (second (first (get-in mc [2 1])))
                     [(f/with-group "close-to-group" (bf/labeled-checkbox "other-asian" "Other Asian"))]))))))

   (question 
    "When it comes to social and political matters, some people think of themselves mainly as White, Chinese, or Black and that is very important to how they think of themselves. Other people don’t tend to think of themselves in these ways. When it comes to social and political matters, how important is your race or ethnicity to how you think of yourself?"
    (bf/radio-group
     :has-ethnic-identity
     {:very-important "Very important"
      :somewhat-important "Somewhat important"
      :not-very-important "Not very important"
      :not-important "Not important at all"}))
   ;; Party ID and vote choice questions
   (single-choice :party-id
                  "In federal politics, do you usually think of yourself as a:"
                  (merge (political-groups subject) {:dk "I don't know"}))

   [:div.election-choice.national-election-choice
    (add-class 
     (yes-no :national-election "Did you vote in the national election in May?")
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

   (question
    [:div
     (when-not (= "none" (:minority-projection subject))
       [:p
        "According to the most recent census the number of visible minorities is going reach "
        (:minority-projection subject)
        "% of the Canadian population in the next 10 years, largely as a result of immigration."])
     [:p "Do you think the number of immigrants from foreign countries who are permitted to come to Canada to live should be increased a little, increased a lot, decreased a little, decreased a lot, or left the same as it is now?"]]
    (bf/radio-group
     :increase-immigration
     {:increase-little "Increased a little"
      :increase-much "Increased a lot"
      :decrease-little "Decreased a little"
      :decrease-much "Decreased a lot"
      :same "Left the same as it is now."}))))

(defscreen racial-ethnic [subject]
;;;Q29.	Question:
  

  (directions 
   "Please read the following statements and for each one, tell us how strongly you agree or disagree.")
;;; 
;;;Q 30, 31. Q32, Q33, Q34, Q35
  (doall
   (map
    (fn [[k p]] (question p (agree-disagree k)))
    {:melting-pot
     "It is better for Canada if different racial and ethnic groups maintain their distinct cultures in a cultural mosaic rather than blend together."

     :fewer-opportunities
     "Members of certain ethnic or racial groups have fewer opportunities to get ahead than other people."

     :vote-ethnic 
     "People should always vote for candidates of their same ethnic or racial background."

     :special-programs
     "Members of particular racial or ethnic groups use special programs to get more benefits than they deserve."

     :happier-with-other-groups
     "People are generally happier when they live and socialize with others of different racial and ethnic backgrounds."

     :shop-ethnic
     (str "Ethnic and racial minorities should always shop in stores owned by " (:ethnic-shop subject) ".")}))

;;;Q36.	Question:
    (question
     (str "How would it make you feel if a close relative of yours were planning to marry a person of different "
          (:outgroup-marry subject)
          " from yours? Would you be very uneasy, somewhat uneasy, or not uneasy at all?")
     (bf/radio-group :marry-ethnic
                     {:very-uneasy "Very uneasy"
                      :somewhat-uneasy "Somewhat uneasy"
                      :not-uneasy "Not uneasy at all"}))
 
;;;Q37.	Question:
    (question
     "How would it make you feel if a close relative of yours were planning to marry a person who had strong political beliefs different from your own. Would you be very uneasy, somewhat uneasy, or not uneasy at all?"
     (bf/radio-group :marry-political
                     {:very-uneasy "Very uneasy"
                      :somewhat-uneasy "Somewhat uneasy"
                      :not-uneasy "Not uneasy at all"}))

;;;Q38, Q39, Q40, Q41
  (directions 
   "Please read the following statements and for each one, tell us how strongly you agree or disagree.")

  (doall
   (map
    (fn [[k p]] (question p (agree-disagree k)))
    {:special-favors 
     "Irish, Italian, Jewish and many other minorities overcame prejudice and worked their way up. Other minorities should do the same without any special favors."

     :try-harder
     "It's really a matter of some people not trying hard enough; if racial and ethnic minorities would only try harder they could be just as well off as whites."

     :social-welfare
     (str "Most " (:get-welfare subject)  " who receive money from social welfare programs could get along without it if they tried.")

     :gov-attention
     "Government officials usually pay less attention to a request or complaint from someone who is a racial or ethnic minority than from someone who is white."})))

(defscreen racial-conflict [subject]
  (question
   "On the whole, do you like or dislike this neighborhood as a place to live. Would you say you like it a lot, like it, dislike it, dislike it a lot?"
   (bf/radio-group :like-neighborhood
                   {:like-alot "Like it a lot."
                    :like "Like it."
                    :dislike "Dislike it."
                    :dislike-alot "Dislike it a lot."}))
  (question
   "How worried are you about your safety in your neighborhood?  Are you very worried, somewhat worried, not very worried, or not at all worried?"
   (bf/radio-group :safe-neighborhood
                   {:worried "Worried"
                    :somewhat-worried "Somewhat worried"
                    :not-very-worried "Not very worried"
                    :not-at-all-worried "Not at all worried"}))

  (question
   "If you could find housing that you liked, would you rather live with neighbors who mostly share your political beliefs and values, or who hold a wide range of political beliefs and values, or is it not important to you?"
   (bf/radio-group
    :housing-political
    {:share "Mostly share my political beliefs and values"
     :diversity "Mostly hold a wide range of political beliefs and values"
     :not-important "This is not important to me"}))
  
  (question
   "What about when it comes to the race and ethnicity of your neighbors? If you could find housing that you liked, would you prefer neighbors who share your racial and ethnic background, or who represent a mix of racial and ethnic backgrounds, or is it not important to you?"
   (bf/radio-group
    :housing-ethnic
    {:same "Mostly share my racial and ethnic background"
     :diversity "Mostly a mix of racial and ethnic backgrounds"
     :not-important "This is not important to me"}))

  (directions
   "For each of the following statements, please tell us how strongly you agree or disagree.")
 
  (doall
   (map
    (fn [[id prompt]] (question prompt (agree-disagree id)))
    {:competition-jobs 
     "More good jobs for people of one racial or ethnic group means fewer good jobs for members of other groups."

     :ethnic-influence
     "The more influence people in one racial or ethnic group have in local politics, the less influence members of other groups will have in local politics."

     :ethnic-housing-pressure
     "As more good housing and neighborhoods go to people in one racial or ethnic group, there will be fewer good houses and neighborhoods for members of other groups."

     :government-improve
     "The government in Ottawa should make every effort to improve the social and economic position of racial and ethnic minorities."

     :government-fair-treatment
     "The government in Ottawa should see to it that racial and ethnic minorities get fair treatment in jobs."}))

  (single-choice {:id "employed-student"} :employed
                 "Are you currently employed or enrolled as a student?"
                 {:employed "I am employed"
                  :student "I am a student"
                  :unemployed "I am not employed"})
  [:div#employment-follow-up
   (question
    "Would you please tell us where you work or study? Please provide the postal code, or if you don't know the postal code, please provide an intersection or address."
    (f/text-field :work-study-address))

;;;Q20.	Question:
   (question 
    "Are the people at your work (or school) mostly white, mostly racial or ethnic minorities, about half and half, or some other mixture?"
    (bf/radio-group :work-ethnicity
                    {:white "Mostly white"
                     :ethnic "Mostly racial or ethnic minorities"
                     :half "About half and half"
                     :other [:span "Some other mixture. Please explain:" (f/text-field :other-description)]}))]

  
  (yes-no :complete-additional-survey "Would you be willing to participate in another survey?"))

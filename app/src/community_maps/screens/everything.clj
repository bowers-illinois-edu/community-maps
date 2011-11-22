(ns community-maps.screens.everything
  (:use community-maps.tags
        shanks.core)
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]
            [community-maps.gis :as gis]))

(defscreen basics
  [subject]
  
  ;;; District Map Related Questions (if we can't look up the
  ;;; district, they are skipped entirely.
  (let [dst (:display-district subject)
        district-id (gis/get-subject-district-id subject dst)]
    (when (not (= 0 district-id))
      (list
       (directions
        (str "Now, look at this map. The highlighted area shows your " (get gis/*districts* dst) ".")
        "Referring to this map with the Census boundary on it, I’d like to ask a series of questions just like the previous ones:")
       (kml-map (gis/kml-url dst district-id))
       
      ;;;Q14.	Question:
       (group-sliders
        :census-community
        "Just your best guess - what percentage of the population in the highlighted area is:")

      ;;;Q15.	Question:
       (learn-about-composition
        :census-composition
        "How did you learn about the composition of this area?")
       
      ;;;Q16.	Question:
       (question
        (str "On the whole, do you like or dislike this "
             (get gis/*districts* dst)
             " as a place to live? Would you say you like it a lot, like it, dislike it, or dislike it a lot?")
        (bf/radio-group
         :like-dislike-census
         {:like-alot "Like it a lot"
          :like "Like it"
          :dislike "Dislike it"
          :dislike-alot "Dislike it a lot"}))

      ;;;Q17.	Question:
       (yes-no :census-feel-community 
               (str "On the whole, do you think that people who live in this "
                    (get gis/*districts* dst)
                    " feel a sense of community?")))))

;;;Q18.	Question:
  (question
   (str "Some political leaders argue that in the next 10 years, ethnic minorities will "
        (subject :minority-population-share)
        " their share of the population in this area by a lot.  Do you think this is a good or bad thing?")
   (bf/radio-group :ethnic-growth {:good "Good thing" :bad "Bad thing"}))

;;;Assuming we know employment status from Vote Compass...
;;;Q19.	Question:
  (question
   "At what location do you work or study? Please include the entire address, including city and province. (Reminder: These surveys are anonymous, and your work/study address will never be used unless you explicitly give us permission to do so.)"
   (f/text-field :work-study-address))

;;;Q20.	Question:
  (question 
   "Are the people at your work (or school) mostly white, mostly ethnic minorities, about half and half, or some other mixture?"
   (bf/radio-group :work-ethnicity
                   {:white "Mostly white"
                    :ethnic "Mostly ethnic minorities"
                    :half "About half and half"
                    :other [:span "Some other mixture. Please explain:" (f/text-field :other-description)]}))
;;;What is the largest nonwhite group?

  (directions
   "Now I'm going to read the names of some groups in Canadian society. When I read the name of a group, we'd like you to tell us if you feel particularly close to people in that group --- if you feel the people in that group are like you in their ideas and interests and feelings about things. If you do not feel particularly close to the people in a group, I'd like to know that, too. If we come to a group you don't know much about, just tell me and we'll move on to the next one.")
;;; 
;;;Q21.	Question:
;;; 
  (let [mc (multiple-choice
            :close-to-group
            "Do you feel close to any of the following groups"
            (merge
             (dissoc ethnic-political-groups :other-asian)
             {:local-community "People in your local community"
              :census-community "People in this area"}))]
    (assoc-in mc [2 1]
              (concat
               (second (first (get-in mc [2 1])))
               [(f/with-group "close-to-group" (bf/labeled-checkbox "other-asian" "Other Asian"))])))


;;;Q26.	Question
  (group-sliders
   :canada-percentages
   "What is your best guess for the percentage of the Canadian population for each of the following groups?")

;;;Q27.	Question:
  (learn-about-composition
   :canada-percentages-learn
   "How did you learn about the composition of Canada?")

;;;Q8.	Question:
  (question
   [:div
    [:p
     "According to a "
     (subject :leader-party),
     " leader, the number of visible minorities is going reach "
     (subject :minority-projection)
     "% of the Canadian population in the next 10 years, largely as a result of immigration."]
    [:p "Do you think the number of immigrants from foreign countries who are permitted to come to Canada to live should be increased a little, increased a lot, decreased a little, decreased a lot, or left the same as it is now?"]]
   (bf/radio-group
    :increase-immigration
    {:increase-little "Increased a little"
     :increase-much "Increased a lot"
     :decrease-little "Decreased a little"
     :decrease-much "Decreased a lot"
     :same "Left the same as it is now."}))

;;;Q28.	Question:
  (group-sliders
   :group-feeling-thermometer
   "We'd also like to get your feelings about some groups in Canadian society. When I read the name of a group, we'd like you to rate it with what we call a feeling thermometer. Ratings between 50 degrees and 100 degrees mean that you feel favorably and warm toward the group; ratings between 0 and 50 degrees mean that you don't feel favorably towards the group and that you don't care too much for that group. If you don't feel particularly warm or cold toward a group you would rate them at 50 degrees. If we come to a group you don't know much about, just tell me and we'll move on to the next one."
   "0" "100")

;;;Q29.	Question:
  (question 
   "When it comes to social and political matters, some people think of themselves mainly as white, Chinese, or Black and that is very important to how they think of themselves. Other people don’t tend to think of themselves in these ways. When it comes to social and political matters, how important is your race or ethnicity to how you think of yourself? Is it very important, somewhat important, not very important, or not important at all?"
   (bf/radio-group
    :has-ethnic-identity
    {:very-important "Very important"
     :somewhat-important "Somewhat important"
     :not-very-important "Not very important"
     :not-important "Not important at all"}))
;;;Q30.	Question:
  (question
   "Some people say that it is better for Canada if different racial and ethnic groups maintain their distinct cultures as in a cultural mosaic. Others say that it is better if groups change so that they blend into the larger society as in the idea of a melting pot. Where would you place yourself on this scale?"
   "Todo: slider")
;;;Responses:
;;;One end of slider: "racial and ethnic groups should maintain their distinct cultures"
;;;Other end: "groups should change so that they blend into the larger society.  

  (directions 
   "Now I’m going to read you some statements and would like to get your reaction to them. After I read each statement, please tell me if you strongly agree, agree, neither agree nor disagree, disagree, or strongly disagree with the statement.")
;;; 
;;;Q31. Q32, Q33, Q34, Q35
  (doall
   (map
    (fn [[k p]] (question p (agree-disagree k)))
    {:fewer-opportunities
     "Members of certain ethnic or racial groups have fewer opportunities to get ahead than other people."

     :vote-ethnic 
     "People should always vote for candidates of their same ethnic background."

     :special-programs
     "Members of particular ethnic groups use special programs to get more benefits than they deserve."

     :happier-within-group
     "People of different ethnic or racial groups are generally happier when they live and socialize with others of the same background."
     :shop-ethnic
     (str "Ethnic minorities should always shop in stores owned by " (subject :ethnic-shop) ".")}))

;;;Q36.	Question:
    (question
     (str "How would it make you feel if a close relative of yours were planning to marry a person of different "
          (subject :outgroup-marry)
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
   "Now I’m going to read you some statements and would like to get your reaction to them. After I read each statement, please tell me if you strongly agree, agree, neither agree nor disagree, disagree, or strongly disagree with the statement.")

  (doall
   (map
    (fn [[k p]] (question p (agree-disagree k)))
    {:special-favors 
     (str "Irish, Italian, Jewish and many other minorities overcame prejudice and worked their way up. Other minorities, like "
          (subject :ethnic-work-up)
          ", should do the same without any special favors.")

     :try-harder
     "It's really a matter of some people not trying hard enough; if ethnic minorities would only try harder they could be just as well off as whites."

     :social-welfare
     (str "Most " (subject :get-welfare)  " who receive money from social welfare programs could get along without it if they tried.")

     :gov-attention
     "Government officials usually pay less attention to a request or complaint from an ethnic minority person than from a white person."}))

;;;Q42.	Question
  (question
   "If you could find the housing that you would want and like, would you rather live in a neighborhood that is mostly [co-partisans], mostly members of other political parties, or some mixture of them?"
   (bf/radio-group
    :housing-political
    {:co-partisans "Mostly co-partisans"
     :other "Mostly members of other political parties"
     :mixture [:span "Some mixture of them. Please explain: " (f/text-field :explain-other)]}))

;;;Q43.	Question:
  (question
   (str "If you could find the housing that you would want and like, would you rather live in a neighborhood that is mostly white, mostly "
        (subject :prefer-neighborhood)
        ", or some other mixture?")
   (bf/radio-group
    :housing-ethnic
    {:white "Mostly white"
     :ethnic (str "Mostly " (subject :prefer-neighborhood))
     :other [:span "Some other mixture. Please explain: " (f/text-field :explain-other)]}))

;;;Q44.	Question:
  (directions
   "Now I have some questions about different groups in our society. (randomize order)"
   "A score of 1 means that you think almost all of the people in that group tend to be \"hard-working.\" A score of 7 means that you think most people in the group are \"lazy.\" A score of 4 means that you think that most people in the group are neither particularly lazy nor particularly hardworking, and of course, you may choose any number in between.")

  (group-sliders
   :hard-working-lazy
   "What do you think of these groups"
   "Lazy"
   "Hardworking")

;;;Q45.	Question:
  (directions 
   "The next set asks if people in each group tend to be \"intelligent\" or \"unintelligent\". A score of 1 means that you think almost all of the people in that group tend to be \"intelligent\". A score of 7 means that you think most people in the group are \"unintelligent.\" A score of 4 means that you think that most people in the group are neither particularly unintelligent nor particularly intelligent, and of course, you may choose any number in between.")
  (group-sliders :intelligent-unintelligent "Where would you rate this groups?"
                 "Intelligent" "Unintelligent")

;;;Q46, Q47, Q48, Q49
  (directions
   "Now I’m going to read you some statements and would like to get your reaction to them. After I read each statement, please tell me if you strongly agree, agree, neither agree nor disagree, disagree, or strongly disagree with the statement.")
 
  (doall
   (map
    (fn [[id prompt]] (question prompt (agree-disagree id)))
    {:competition-jobs 
     "More good jobs for people of one ethnic group means fewer good jobs for members of other groups."

     :ethnic-influence
     "The more influence people in one ethnic group have in local politics, the less influence members of other groups will have in local politics."

     :ethnic-housing-pressure
     "As more good housing and neighborhoods go to people in one ethnic group, there will be fewer good houses and neighborhoods for members of other groups."

     :healthcare-spending
     "The more money spent on doctors, hospitals, and medicine for the healthcare of people in one ethnic group, the less money that will be available for the healthcare of members of other groups."}))

;;;Q50.	Question:
  (seven-point-scale :government-improve-ethnic "Government should intervene" "Not the government's job"
   "Some people feel that the government in Ottawa  should make every effort to improve the social and economic position of ethnic minorities. Suppose these people are at one end of a scale, at point 1. Others feel that the government should not make any special effort to help ethnic minorities because they should help themselves. Suppose these people are at the other end, at point 7. And, of course, some other people have opinions somewhere in between. Where would you place yourself on this scale?")

;;;Q51.	Question:
  (question
   [:div
    [:p "Some people feel that if ethnic minorities are not getting fair treatment in jobs, the government in Ottawa ought to see to it that they do. Others feel that this is not the federal government's business."]
    [:p "How do you feel? Should the government in Ottawa see to it that ethnic minorities get fair treatment in jobs or is this not the federal government's business?"]]
   (agree-disagree :government-ensure-fair-treatment))


  (yes-no :complete-additional-survey "Would you be willing to complete another survey?"))

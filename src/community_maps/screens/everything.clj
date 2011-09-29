(ns community-maps.screens.everything
  (:use community-maps.tags
        shanks.core)
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]))

(defn directions
  "Provide a set directions inline with the questions"
  [& body]
  [:div.directions (doall (map #(vector :p %) body))])

(defscreen basics
  [subject]
  
;;;Q1.
  (question "In a moment, we will ask you to look at where you live on a map.
Please tell us your address. (Reminder: These surveys are anonymous, and your
address will never be used unless you explicitly give us permission to do so.)"
            (f/text-field :address (:address subject))) ; if preloaded 

;;;Q2.	Question: 
  (question "How long have you lived in your current home?"
            (bf/radio-group :length-of-residence
                         {:less-than-one "Less than one year"
                          :x-years (f/text-field :x-years)
                          :all-my-life "All my life"}))
;;;Q3.	Question: 

  (question
   "What city and province did you live in before moving to this home? Please select the best description and fill in the name of the location below."
   (bf/radio-group :other-residence
                   {:city "Elsewhere in the same city"
                    :province "Elsewhere in the same province"
                    :other "In another province"
                    :ex-canada "Outside Canada"})
   (f/text-field :other-residence-name))

;;;Q4.	Question:

  (single-choice
   :rent-own
   "Do you or your family own your own home/apartment, pay rent or what?"
   {:yes "We own"
    :rent "We rent"
    :other "We have another arrangement"})

;;;Q5.	Question:
;;;Randomize:
;;;a)	Question about whether you voted in the recent provincial election. Follow – which party did you vote for.
;;;b)	2 questions re neighborhood:
;;;a.	On the whole, do you like or dislike this neighborhood as a place to live. Would you say you like it a lot, like it, dislike it, dislike ti a lot?
;;;b.	How worried are you about your safety in your neighborhood?  Are you very worried, somewhat worried, not very worried, or not at all worried?
;;;c)	 Question about whether you voted in the national election in May. Follow – which party did you vote for.
;;;d)	no prompt.
;;; 
;;; 
;;;Q5a. Map-drawing question
;;; 
;;;[IF HAVING TROUBLE: If you are having trouble with the drawing, please name major cross streets, parks, stores or other landmarks that you think of as boundaries of your local community.]
;;; 
  ; Some inter-question directions:
  (directions "Now, for these next few questions we’ll be referring to the
areas you highlighted")

;;;Q6.	Question
  (percentage-of-community
   :community-percentage
   "Just your best guess – what percentage of the population of this community is:")
;;; 
;;;Q7.	Question:
  (learn-about-composition
   :community-composition
   "How did you learn about the composition of your local community?")
;;;[IF R ASKS: Composition is the percentage of whites, unemployed, etc. in your local community.]
;;; 
;;;if more than one is checked, which is the main source of information.
;;;Does this differ by target (race, party, unemployment)

  (directions "Here are some statements about things that people in your local community that you have drawn on this map [REFERRING TO MAP THEY DREW ON] may or may not do. For each of these statements, please tell me whether you strongly agree, agree, neither agree nor disagree, disagree, or strongly disagree.")

;;;Q9, Q10, Q11	

  (doall
   (map
    (fn [[id prompt]] (question prompt (agree-disagree id)))
    {:help "People around here are willing to help others in their community."
     :get-along "People in this community generally don’t get along with each other."
     :share-values "People in this community do not share the same values."}))

  (directions "For each of the following, please tell me if it is very likely, likely, unlikely or very unlikely that people in your community would act in the following manner.")

;;;Q12., Q13.
  (doall
   (map
    (fn [[id prompt]] (question prompt (likelihood id)))
    {:graffiti "If some children were painting graffiti on a local building or house, how likely is it that people in your community would do something about it?"
     :community-organize "Suppose that because of budget cuts the fire station or library closest to your home was going to be closed down by the city. How likely is it that community residents would organize to try to do something to keep the fire station open?"}))

  (directions
   "Now, look at this map [SHOW HIGHLIGHTED Province/City/Dissemination Area MAP]. The highlighted area shows [your Province/ your City/ what the Census bureau defines as your dissemination area]."
   "Referring to this map with the Census boundary on it, I’d like to ask a series of questions just like the previous ones:")

;;;Q14.	Question:
  (percentage-of-community
   :census-community
   "Just your best guess - what percentage of the population in the highlighted area is:")

;;;Q15.	Question:
  (learn-about-composition
   :census-composition
   "How did you learn about the composition of this area?")
 
;;;Q16.	Question:
  (question
   "On the whole, do you like or dislike this [province/city/Dissemination area] as a place to live? Would you say you like it a lot, like it, dislike it, or dislike it a lot?"
   (bf/radio-group
    :like-dislike-census
    {:like-alot "Like it a lot"
     :like "Like it"
     :dislike "Dislike it"
     :dislike-alot "Dislike it a lot"}))

;;;Q17.	Question:
  (yes-no :census-feel-community 
          "On the whole, do you think that people who live in this [province/city/Dissemination area] feel a sense of community?")

;;;Q18.	Question:
  (question
   "Some political leaders argue that in the next 10 years, ethnic minorities will [increase their share of the population in this area by a lot / decrease their share of the population in this area by a lot].  Do you think this is a good or bad thing?"
   (bf/radio-group :ethnic-growth {:good "Good thing" :bad "Bad thing"}))

;;;Assuming we know employment status from Vote Compass...
;;;Q19.	Question:
  (question
   "At what location do you work or study?"
   (f/text-field :work-study-address)
   [:em.address-note "number and street name or nearest intersection"])

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
  (multiple-choice
   :close-to-group
   "Do you feel close to any of the following groups"
   (merge
    ethnic-political-groups
    {:local-community "People in your local community"
     :census-community "People in this area"}))

  (directions "Now let’s look at the map of your local community again. [REFERRING TO MAP THEY DREW ON]")

;;;Q22.	Question:
  (question 
   "Thinking about your local community: is it mostly white, mostly ethnic minorities, about half and half, or some other mixture of people?"
   (bf/radio-group :community-ethnic-makeup
                   {:white "Mostly white"
                    :ethnic "Mostly ethnic minorities"
                    :half "About half and half"
                    :other [:span "Some other mixture. Please explain: " (f/text-field :other-description)]}))
;;;What is the largest nonwhite group?
;;; 
;;;Q23.	Question: 
  (single-choice
   :community-political-makeup
   "Thinking about your local community, is it mostly:"
   {:conservatives "Conservatives"
    :liberals "Liberals"
    :ndp "NDP"
    :quebec "Bloc Quebecois"
    :other [:span "Some other mixture. Please explain: " (f/text-field :other-description)]})
  
;;;Q24.	Question:
  (yes-no :ethnic-friends "Are any of your friends of a different race or ethnic background than you?")
;;; 
;;;If yes, then Q24a
;;;Q24a. Question:
  (question 
   "Are your friends mostly white, mostly ethnic minorities, about half and half, or some other mixture of people?"
   (bf/radio-group
    :ethnic-friends-composition
    {:white "Mostly white"
     :ethnic "Mostly ethnic minorities"
     :half "About half and half"
     :other [:span "Some other mixture. Please explain:" (f/text-field :other-description)]}))
;;;What is the largest nonwhite group?

;;;Q25.	Question:
  (question 
   "Are your friends mostly conservatives, mostly liberals, mostly NDP, or some other mixture? [if Quebec, add \"mostly Bloc Quebecois\"]"
   (bf/radio-group
    :political-friends-composition
    {:conservative "Mostly conservatives"
     :liberal "Mostly liberal"
     :ndp "Mostly NDP"
     :quebec "Mostly Bloc Quebecois"
     :other [:span "Some other mixture. Please explain:" (f/text-field :other-description)]}))

;;;Q26.	Question
  (percentage-of-community
   :canada-percentages
   "What is your best guess for the percentage of the Canadian population for each of the following groups?")

;;;Q27.	Question:
  (learn-about-composition
   :canada-percentages-learn
   "How did you learn about the composition of Canada?")

;;;Q8.	Question:
  (question
   [:div
    [:p "According to [Conservative / NDP/ BQ/ Liberal leader], the number of visible minorities is going  reach [25%/ 35%/50%] of the Canadian population in the next 10 years, largely as a result of immigration."]
    [:p "Do you think the number of immigrants from foreign countries who are permitted to come to Canada to live should be increased a little, increased a lot, decreased a little, decreased a lot, or left the same as it is now?"]]
   (bf/radio-group
    :increase-immigration
    {:increase-little "Increased a little"
     :increase-much "Increased a lot"
     :decrease-little "Decreased a little"
     :decrease-much "Decreased a lot"
     :same "Left the same as it is now."}))

;;;Q28.	Question:
  (percentage-of-community
   :group-feeling-thermometer
   "We'd also like to get your feelings about some groups in Canadian society. When I read the name of a group, we'd like you to rate it with what we call a feeling thermometer. Ratings between 50 degrees and 100 degrees mean that you feel favorably and warm toward the group; ratings between 0 and 50 degrees mean that you don't feel favorably towards the group and that you don't care too much for that group. If you don't feel particularly warm or cold toward a group you would rate them at 50 degrees. If we come to a group you don't know much about, just tell me and we'll move on to the next one.")

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
     "Ethnic minorities should always shop in stores owned by [ethnic minorities/ other members of their same ethnic background]"}))

;;;Q36.	Question:
    (question
     "How would it make you feel if a close relative of yours were planning to marry a person of different [race / ethnic background] from yours? Would you be very uneasy, somewhat uneasy, or not uneasy at all?"
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
     "Irish, Italian, Jewish and many other minorities overcame prejudice and worked their way up. Other minorities, like [Chinese / Blacks] should do the same without any special favors."

     :try-harder
     "It's really a matter of some people not trying hard enough; if ethnic minorities would only try harder they could be just as well off as whites."

     :social-welfare
     "Most [ethnic minorities / people] who receive money from social welfare programs could get along without it if they tried."

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
   "If you could find the housing that you would want and like, would you rather live in a neighborhood that is mostly white, mostly [ethnic minorities/ blacks/ Asian], or some other mixture?"
   (bf/radio-group
    :housing-ethnic
    {:white "Mostly white"
     :ethnic "Mostly [ethnic...]"
     :other [:span "Some other mixture. Please explain: " (f/text-field :explain-other)]}))

;;;Q44.	Question:
  (directions
   "Now I have some questions about different groups in our society. (randomize order)"
   "A score of 1 means that you think almost all of the people in that group tend to be \"hard-working.\" A score of 7 means that you think most people in the group are \"lazy.\" A score of 4 means that you think that most people in the group are neither particularly lazy nor particularly hardworking, and of course, you may choose any number in between.")

  (doall
   (map
    (fn [[id grp]]
      (seven-point-scale id
       (str "Where would you rate " grp " in general on this scale?")))
    (shuffle (vec
              {:whites "Whites"
               :liberals "Liberals"
               :conservatives "Conservatives"
               :blacks "Blacks"
               :chinese "Chinese"
               :ndp "Members of the NDP"
               :indian "East Indians"
               :quebec "members of the Block Quebecois"}))))

;;;Q45.	Question:
  (directions 
   "The next set asks if people in each group tend to be \"intelligent\" or \"unintelligent\". A score of 1 means that you think almost all of the people in that group tend to be \"intelligent\". A score of 7 means that you think most people in the group are \"unintelligent.\" A score of 4 means that you think that most people in the group are neither particularly unintelligent nor particularly intelligent, and of course, you may choose any number in between.")
  (doall
   (map
    (fn [[id grp]]
      (seven-point-scale id (str "Where would you rate " grp " in general on this scale?")))
    (shuffle (vec
              {:whites "Whites"
               :liberals "Liberals"
               :conservatives "Conservatives"
               :blacks "Blacks"
               :chinese "Chinese"
               :ndp "Members of the NDP"
               :indian "East Indians"
               :quebec "members of the Block Quebecois"}))))

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
  (seven-point-scale :government-improve-ethnic 
   "Some people feel that the government in Ottawa  should make every effort to improve the social and economic position of ethnic minorities. Suppose these people are at one end of a scale, at point 1. Others feel that the government should not make any special effort to help ethnic minorities because they should help themselves. Suppose these people are at the other end, at point 7. And, of course, some other people have opinions somewhere in between. Where would you place yourself on this scale?")

;;;
;;;Q51.	Question:
;;;Some people feel that if ethnic minorities are not getting fair treatment in jobs, the government in Ottawa ought to see to it that they do. Others feel that this is not the federal government's business. 
;;; 
;;;How do you feel? Should the government in Ottawa see to it that ethnic minorities get fair treatment in jobs or is this not the federal government's business? 
;;; 
;;;[AFTER R RESPONDS] Do you feel [IT IS/IS NOT] the government’s business strongly or not strongly?
;;; 
;;;Responses:
;;; 
;;; 
;;; 
;;;Ask respondents if they would be willing to complete another survey.
;;;        -replicate local community map-drawing
;;;        -conduct a series of PD games with co-ethnics and non-co-ethnics
     )

(ns community-maps.screens.everything
  (:use community-maps.tags
        shanks.core)
  (:require [hiccup.form-helpers :as f]
            [burp.forms :as bf]))

(defn directions
  "Provide a set directions inline with the questions"
  [& body]
  [:p.directions body])

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
  (question 
   "Just your best guess – what percentage of the population of this community is:"
   (f/with-group :community-racial-percentage
     [:table 
      (doall
       (map
        (fn [[group-id group]] [:tr [:td group] [:td (percentage group-id)]])
        {:black "Black"
         :white "White"
         :liberal "Liberal"
         :conservative "Conservative"
         :unemployed "Unemployed"
         :ndp "NDP"
         :chinese "Chinese"
         :east-indian "East Indian"
         :aboriginal "Canadian Aboriginal"
         :latin "Latin American"
         :other-asian "Other Asian"
         :quebecois "Bloq Quebecois"}))]))
;;; 
;;;Q7.	Question:
  (multiple-choice
   :composition
   "How did you learn about the composition of your local community?"
   {:observation "personal observation"
    :friends "friends and families"
    :news "news (tv, radio, online, paper)"
    :institutions "local institutions"
    :leaders "political leaders"
    :tv "television entertainment shows"})
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

;;;Q14.	Question:
;;;Now, look at this map [SHOW HIGHLIGHTED Province/City/Dissemination Area MAP]. The highlighted area shows [your Province/ your City/ what the Census bureau defines as your dissemination area]. 
;;; 
;;;Referring to this map with the Census boundary on it, I’d like to ask a series of questions just like the previous ones:  
;;; 
;;;Just your best guess - what percentage of the population in the highlighted area is…[Randomize order of groups}
;;; 
;;;Responses:
;;; 
;;;GROUPS
;;;PERCENTAGE
;;;a. BLACK
;;; 
;;;b.WHITE?
;;; 
;;;c. Liberal
;;; 
;;;d. Conservative
;;; 
;;;e.	UNEMPLOYED?
;;;f. NDP?
;;;g. Chinese
;;;h. East Indian
;;;i. Canadian Aboriginal
;;;j. Latin American
;;;k. Other Asian (has to be asked last)
;;;l. Bloq Quebecois (only asked of Rs in Quebec)
;;; 
;;;Q15.	Question:
;;;How did you learn about the composition of this area?
;;; 
;;;[IF R ASKS: Composition is the percentage of whites, blacks, etc. 
;;; 
;;;Responses:
;;;personal observation
;;;friends and families
;;;news (tv, radio, online, paper)
;;;local institutions
;;;political leaders
;;;television entertainment shows
;;; 
;;;if more than one is checked, which is most important
;;;Does this differ by target (race, party, unemployment)
;;; 
;;; 
;;;
;;;Q16.	Question:
;;;On the whole, do you like or dislike this [province/city/Dissemination area] as a place to live? Would you say you like it a lot, like it, dislike it, or dislike it a lot?
;;; 
;;; 
;;;Q17.	Question:
;;;On the whole, do you think that people who live in this [province/city/Dissemination area] feel a sense of community?  
;;; 
;;;Q18.	Question:
;;;Some political leaders argue that in the next 10 years, ethnic minorities will [increase their share of the population in this area by a lot / decrease their share of the population in this area by a lot].  Do you think this is a good or bad thing?
;;; 
;;; 
;;;Assuming we know employment status from Vote Compass...
;;;Q19.	Question:
;;;At what location do you work or study?
;;; 
;;;address: number and street name or nearest intersection
;;; 
;;; 
;;; 
;;; 
;;;
;;;Q20.	Question:
;;;Are the people at your work (or school) mostly white, mostly ethnic minorities, about half and half, or some other mixture?
;;; 
;;; 
;;;[IF R RESPONDS A MIXTURE: Can you explain what you mean by “some other mixture”?]
;;; 
;;;What is the largest nonwhite group?
;;; 
;;; 
;;; 
;;;
;;;Q21.	Question:
;;;Now I'm going to read the names of some groups in Canadian society. When I read the name of a group, we'd like you to tell us if you feel particularly close to people in that group --- if you feel the people in that group are like you in their ideas and interests and feelings about things. If you do not feel particularly close to the people in a group, I'd like to know that, too. If we come to a group you don't know much about, just tell me and we'll move on to the next one.
;;; 
;;; 
;;;Do you feel close to [GROUP]? (MARK ALL MENTIONS: yes or no)(randomize order of groups)
;;; 
;;;Responses:
;;;GROUPS
;;; 
;;;a.liberals
;;; 
;;;b. whites
;;; 
;;;c. blacks
;;; 
;;;d. conservatives
;;; 
;;;e. members of NDP
;;; 
;;;f. Chinese
;;;g. East Indian
;;;h. unemployed
;;;i. immigrants
;;;j. Bloc Quebecois
;;;k. Latin Americans
;;;l. Canadian Aboriginals
;;; 
;;;m.	PEOPLE IN YOUR LOCAL COMMUNITY [referring to the map they drew on]
;;; 
;;;n	PEOPLE IN THIS AREA [referring to the province/city/DA map]
;;; 
;;; 
;;;
;;;Q22.	Question:
;;;Now let’s look at the map of your local community again. [REFERRING TO MAP THEY DREW ON]
;;; 
;;;Thinking about your local community: is it mostly white, mostly ethnic minorities, about half and half, or some other mixture of people? 
;;; 
;;;[IF R RESPONDS A MIXTURE: Can you explain what you mean by “some other mixture”?]
;;; 
;;;What is the largest nonwhite group?
;;; 
;;; 
;;; 
;;;Q23.	Question: 
;;;[REFERRING TO THE MAP THEY DREW ON]
;;;Thinking about your local community: is it mostly conservatives, mostly liberals, mostly NDP, or some other mixture? [if Quebec, add "mostly Bloc Quebecois"]
;;; 
;;;[IF R RESPONDS A MIXTURE: Can you explain what you mean by “some other mixture”?]
;;; 
;;; 
;;; 
;;; 
;;;
;;;Q24.	Question:
;;;Are any of your friends of a different race or ethnic background than you?
;;; 
;;;If yes, then Q24a
;;; 
;;;Q24a. Question:
;;;Are your friends mostly white, mostly ethnic minorities, about half and half, or some other mixture of people?
;;; 
;;;[IF R RESPONDS A MIXTURE: Can you explain what you mean by “some other mixture”?]
;;; 
;;;What is the largest nonwhite group?
;;; 
;;; 
;;;Q25.	Question:
;;;Are your friends mostly conservatives, mostly liberals, mostly NDP, or some other mixture? [if Quebec, add "mostly Bloc Quebecois"]
;;; 
;;;[IF R RESPONDS A MIXTURE: Can you explain what you mean by “some other mixture”?]
;;; 
;;; 
;;;
;;;Q26.	Question
;;;Now I’d like to talk about the country as a whole. (randomize groups)
;;; 
;;;Just your best guess-what percentage of the Canadian population is black? 
;;; 
;;;Responses:
;;; 
;;;GROUP
;;;PERCENTAGE
;;; 
;;;a. BLACK
;;; 
;;; 
;;;b. WHITE?
;;; 
;;;c. Chinese
;;;d. East Indian
;;;e. Latin American
;;;f. Canadian Aboriginal
;;;g. unemployed
;;; 
;;;h liberal?
;;;i. conservative
;;;j. NDP
;;;k other Asian
;;; 
;;;l. Bloc Quebecois
;;; 
;;; 
;;;
;;;Q27.	Question: 
;;;How did you learn about the composition of Canada?
;;; 
;;;[IF R ASKS: Composition is the percentage of whites, blacks, Democrats, Republicans, and unemployed in your local community.]
;;; 
;;;Responses:
;;;personal observation
;;;friends and families
;;;news (tv, radio, online, paper)
;;;local institutions
;;;political leaders
;;;television entertainment shows
;;; 
;;;if more than one is checked, which is most important
;;;Does this differ by target (race, party, unemployment)
;;; 
;;;Q8.	Question:
;;;According to [Conservative / NDP/ BQ/ Liberal leader], the number of visible minorities is going  reach [25%/ 35%/50%] of the Canadian population in the next 10 years, largely as a result of immigration.  
;;; 
;;;Do you think the number of immigrants from foreign countries who are permitted to come to Canada to live should be increased a little, increased a lot, decreased a little, decreased a lot, or left the same as it is now?  
;;; 
;;; 
;;;Q28.	Question:
;;;Please turn to page 5 in your booklet.
;;;We'd also like to get your feelings about some groups in Canadian society. When I read the name of a group, we'd like you to rate it with what we call a feeling thermometer. Ratings between 50 degrees and 100 degrees mean that you feel favorably and warm toward the group; ratings between 0 and 50 degrees mean that you don't feel favorably towards the group and that you don't care too much for that group. If you don't feel particularly warm or cold toward a group you would rate them at 50 degrees. If we come to a group you don't know much about, just tell me and we'll move on to the next one.
;;; 
;;;Using the scale, how would you rate [GROUP]:
;;; 
;;;List of Groups
;;;GROUPS
;;;00-100.
;;;DEGREES
;;;998. DON’T KNOW
;;;999. OTHER
;;;990. REFUSED
;;;a. liberals
;;;b. conservatives
;;;c. NDP
;;;d. BQ
;;; 
;;; 
;;; 
;;; 
;;; 
;;;e. WHITES
;;; 
;;; 
;;; 
;;; 
;;;f. Chinese
;;;g. East Indian
;;;h. Latin American
;;;i. Blacks
;;;j. Canadian Aboriginal
;;; 
;;; 
;;; 
;;; 
;;;k.	other Asian
;;; 
;;; 
;;; 
;;; 
;;;l.	immigrants 
;;; 
;;; 
;;; 
;;; 
;;;m.	unemployed 
;;; 
;;; 
;;; 
;;; 
;;; 
;;;
;;;Q29.	Question:
;;;When it comes to social and political matters, some people think of themselves mainly as white, Chinese, or Black and that is very important to how they think of themselves. Other people don’t tend to think of themselves in these ways. When it comes to social and political matters, how important is your race or ethnicity to how you think of yourself? Is it very important, somewhat important, not very important, or not important at all?
;;; 
;;;Responses:
;;; 
;;; 
;;;
;;;Q30.	Question:
;;;Some people say that it is better for Canada if different racial and ethnic groups maintain their distinct cultures as in a cultural mosaic. Others say that it is better if groups change so that they blend into the larger society as in the idea of a melting pot. Where would you place yourself on this scale?
;;; 
;;;Responses:
;;;One end of slider: "racial and ethnic groups should maintain their distinct cultures"
;;;Other end: "groups should change so that they blend into the larger society.  
;;; 
;;;Q31.	Question:
;;;Please turn to page 3 in your booklet.
;;;Now I’m going to read you some statements and would like to get your reaction to them. After I read each statement, please tell me if you strongly agree, agree, neither agree nor disagree, disagree, or strongly disagree with the statement.
;;; 
;;;Members of certain ethnic or racial groups have fewer opportunities to get ahead than other people.
;;; 
;;;Responses:
;;; 
;;; 
;;;Q32.	Question:
;;;People should always vote for candidates of their same ethnic background.
;;; 
;;; 
;;;Responses:
;;; 
;;; 
;;;Q33.	Question:
;;;Members of particular ethnic groups use special programs to get more benefits than they deserve.
;;; 
;;;Responses:
;;; 
;;; 
;;;
;;;Q34.	Question:
;;;People of different ethnic or racial groups are generally happier when they live and socialize with others of the same background.
;;; 
;;;Responses:
;;; 
;;; 
;;;Q35.	Question:
;;;Ethnic minorities should always shop in stores owned by [ethnic minorities/ other members of their same ethnic background]
;;; 
;;;Responses:
;;; 
;;; 
;;;
;;;Q36.	Question:
;;;How would it make you feel if a close relative of yours were planning to marry a person of different [race / ethnic background] from yours? Would you be very uneasy, somewhat uneasy, or not uneasy at all?
;;; 
;;; 
;;;Responses:
;;; 
;;; 
;;; 
;;;Q37.	Question:
;;;How would it make you feel if a close relative of yours were planning to marry a person who had strong political beliefs different from your own. Would you be very uneasy, somewhat uneasy, or not uneasy at all?
;;; 
;;;Responses:
;;; 
;;; 
;;; 
;;;
;;;Q38.	Question:
;;;Now I’m going to read you some statements and would like to get your reaction to them. After I read each statement, please tell me if you strongly agree, agree, neither agree nor disagree, disagree, or strongly disagree with the statement.
;;; 
;;;Question:
;;;Irish, Italian, Jewish and many other minorities overcame prejudice and worked their way up. Other minorities, like [Chinese / Blacks] should do the same without any special favors.
;;; 
;;;Responses:
;;; 
;;; 
;;;
;;;Q39.	Question:
;;;It's really a matter of some people not trying hard enough; if ethnic minorities would only try harder they could be just as well off as whites.
;;; 
;;;Q40.	Question:
;;;Most [ethnic minorities / people] who receive money from social welfare programs could get along without it if they tried.
;;; 
;;;Responses:
;;; 
;;; 
;;;Q41.	Question:
;;;Government officials usually pay less attention to a request or complaint from an ethnic minority person than from a white person.
;;; 
;;;Responses: 
;;; 
;;; 
;;;Q42.	Question
;;;If you could find the housing that you would want and like, would you rather live in a neighborhood that is mostly [co-partisans], mostly members of other political parties, or some mixture of them? 
;;; 
;;;Does this work in Canada?
;;;[IF R RESPONDS A MIXTURE: Can you explain what you mean by “some other mixture”?]
;;; 
;;; 
;;; 
;;;Q43.	Question:
;;;If you could find the housing that you would want and like, would you rather live in a neighborhood that is mostly white, mostly [ethnic minorities/ blacks/ Asian], or some other mixture? 
;;; 
;;;[IF R RESPONDS A MIXTURE: Can you explain what you mean by “some other mixture”?]
;;; 
;;;Responses:
;;; 
;;; 
;;;Q44.	Question:
;;;Now I have some questions about different groups in our society. (randomize order)
;;; 
;;;A score of 1 means that you think almost all of the people in that group tend to be "hard-working." A score of 7 means that you think most people in the group are "lazy." A score of 4 means that you think that most people in the group are neither particularly lazy nor particularly hardworking, and of course, you may choose any number in between.
;;; 
;;; 
;;;a. Where would you rate Whites in general on this scale?
;;;b. Where would you rate Liberals in general on this scale?
;;;c.  Where would you rate Conservatives in general on this scale?
;;;d. Where would you rate Blacks in general on this scale?
;;; 
;;;e. Where would you rate Chinese in general on this scale?
;;; 
;;;f. Where would you rate members of the NDP in general on this scale?
;;; 
;;;g. Where would you rate East Indians in general on this scale?
;;; 
;;;h. Where would you rate members of the Block Quebecois on this scale?
;;; 
;;;
;;;Q45.	Question:
;;;Please turn to the next page (page 8) in your booklet.
;;;The next set asks if people in each group tend to be “intelligent" or "unintelligent". A score of 1 means that you think almost all of the people in that group tend to be “intelligent”. A score of 7 means that you think most people in the group are “unintelligent.” A score of 4 means that you think that most people in the group are neither particularly unintelligent nor particularly intelligent, and of course, you may choose any number in between. 
;;; 
;;;a. Where would you rate Whites in general on this scale?
;;;b. Where would you rate Liberals in general on this scale?
;;;c.  Where would you rate Conservatives in general on this scale?
;;;d. Where would you rate Blacks in general on this scale?
;;; 
;;;e. Where would you rate Chinese in general on this scale?
;;; 
;;;f. Where would you rate members of the NDP in general on this scale?
;;; 
;;;g. Where would you rate East Indians in general on this scale?
;;; 
;;;h. Where would you rate members of the Block Quebecois on this scale?
;;; 
;;; 
;;; 
;;;
;;;Q46.	Question: 
;;;Now I’m going to read you some statements and would like to get your reaction to them. After I read each statement, please tell me if you strongly agree, agree, neither agree nor disagree, disagree, or strongly disagree with the statement.
;;; 
;;;More good jobs for people of one ethnic group means fewer good jobs for members of other groups.
;;; 
;;;
;;;Q47.	Question:
;;;The more influence people in one ethnic group have in local politics, the less influence members of other groups will have in local politics.
;;; 
;;;Responses:
;;; 
;;; 
;;;Q48.	Question:
;;;As more good housing and neighborhoods go to people in one ethnic group, there will be fewer good houses and neighborhoods for members of other groups.
;;; 
;;;Responses:
;;; 
;;; 
;;;Q49.	Question:
;;;The more money spent on doctors, hospitals, and medicine for the healthcare of people in one ethnic group, the less money that will be available for the healthcare of members of other groups .
;;;
;;;Q50.	Question:
;;;Some people feel that the government in Ottawa  should make every effort to improve the social and economic position of ethnic minorities. Suppose these people are at one end of a scale, at point 1. Others feel that the government should not make any special effort to help ethnic minorities because they should help themselves. Suppose these people are at the other end, at point 7. And, of course, some other people have opinions somewhere in between. Where would you place yourself on this scale?
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

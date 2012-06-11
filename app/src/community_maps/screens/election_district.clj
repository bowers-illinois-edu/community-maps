(ns community-maps.screens.election-district
  (:use shanks.core
        [community-maps tags util gis]
        community-maps.previous-survey
        clojure.contrib.strint))

(defscreen election-district-drawing
  [subject]
  (let [[vcid zoom lat lng] (drawing-data-pid (:pid subject))]
    (list
     (directions
      "Imagine you are in charge of drawing the district boundaries for your riding. This district will define who is eligible to represent your community in the House of Commons."
      (if (= "sortition" (:election-district-type subject))
        "However, imagine that instead of electing a candidate, any citizen 18 years or older in your riding can put his or her name up for consideration, and one person will be <strong>randomly selected</strong> to represent this district."
        "Any citizen 18 years or older can stand for election, and the candidate with the largest share of the vote will be elected to represent this district. This district also defines who can vote for candidates for office.")
      "On the map below, please draw a district that you think would lead to the best possible person being sent to Ottawa to represent your community in the House of Commons.")
     (scribble-map :outcome lat lng 12))))

(def district-questions
  (list
   (likelihood :know-rep "How likely would it be that you would personally know the person representing the district?")

   (likelihood :take-call "If you called the representative on the phone, or sent an email, how likely would he or she be to take your call or return your email?")

   (ordered-choice
    :hops-to-rep
    "If you had to pass a letter to the representative, and you could only deliver to another person, and he or she then also had to deliver to another person, and so on until it reached the representative, how many people would it have to pass through before it reached the representative?"
    {:one "I could give it directly to the representative"
     :two "A friend could give it to the representative"
     :three "A friend of a friend could give it to the representative"
     :more "At least three people would have to touch it before it reached the representative"})

   (yes-no :know-media "Do you know anyone who works in a media organization serving this area (example media: television, radio, newspaper, widely read website)?")))

(defscreen election-district-answers
  [subject]
  (directions
   (<< "Here is the map you drew of your ideal district. We will now ask you some questions about it. Remember that this district decides who will represent you in the House of Commons, and that person is determined by ~{(if (= \"sortition\" (:election-district-type subject)) \"a lottery\" \"an election\")}." )
   (<< "While there is not an actual person representing this district, please imagine what type of person you think would be  ~{(if (= \"sortition\" (:election-district-type subject)) \"randomly selected\" \"elected\")}."))

  (static-map-communities subject :election-district-drawing-outcome-data)

  district-questions)

(defscreen election-district-real-fed
  [subject]
  (let [[vcid zoom lat lng] (drawing-data-pid (:pid subject))]
    (list
     (directions
      "Here is your real federal election district. We would now like to ask you some questions as they relate to this district."
      "Imagine that an election has been recently held, and a new person has been elected to the House of Commons to represent this district. Imagine what kind of person would be elected to represent your district when answering the following questions.")
     (kml-map (kml-url "fed" (get-subject-district-id lat lng "fed")))
     district-questions)))


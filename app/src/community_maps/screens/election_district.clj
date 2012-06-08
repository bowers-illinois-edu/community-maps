(ns community-maps.screens.election-district
  (:use shanks.core
        community-maps.tags
        community-maps.previous-survey))

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


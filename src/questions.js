/*******************************************************************************
 * Code for handling questions and paging in the demo.
 * 
 * (c) 2011 Mark M. Fredrickson
 *
 * Requires jQuery and Google Maps (v3) to be loaded already.
 *
 ******************************************************************************/

// function to serialize form data to JS object
// http://stackoverflow.com/questions/1184624/serialize-form-to-json-with-jquery

jQuery.fn.serializeObject = function() {
  var arrayData, objectData;
  arrayData = this.serializeArray();
  objectData = {};

  $.each(arrayData, function() {
    var value;

    if (this.value != null) {
      value = this.value;
    } else {
      value = '';
    }

    if (objectData[this.name] != null) {
      if (!objectData[this.name].push) {
        objectData[this.name] = [objectData[this.name]];
      }

      objectData[this.name].push(value);
    } else {
      objectData[this.name] = value;
    }
  });

  return objectData;
};

$(document).ready(function() {
  
  var questions = $("#survey-questions").children();

  for(j = 0; j < questions.length; j++) {
    (function(i) {
      var current = $(questions[i]);
      if (i > 0) {
        // add previous link
        var link = makeButton("Previous").click(function() {
          current.fadeOut("slow", function(){
            $(questions[i - 1]).fadeIn("slow"); 
          });
        });
        current.children().first().append(link);
      } 

      if (i < questions.length - 1) {
        var link = makeButton("Next").click(function() {
          current.fadeOut("slow", function(){
            $(questions[i + 1]).fadeIn("slow");
          });
        });

        current.children().first().append(link);
      }

    })(j); // work around for JS scoping issue with for loops
  }

    // add fancy sliders questions
  $(".slider-container").each(function() {
    var s = $(this);
    var lbl = $("label", s);
    var txt = $("input", s);
    var sld = $(".slider", s);
    var start = Math.floor(Math.random() * 100);
    sld.slider({
      value: start,
      min: 0,
      max: 100,
      step: 1,
      slide: function(e, ui) {txt.val(ui.value + '%');}
    });
    txt.val(start + '%');
  }); 

  // the questions will appear when the training and drawing is done. see main.js
  questions.hide();
  

});

/*******************************************************************************
 * Local question type support JavaScript
 * 
 * (c) 2011 Mark M. Fredrickson
 *
 * Requires jQuery and Google Maps (v3) to be loaded already.
 *
 ******************************************************************************/


jQuery(document).ready(function() {
  $("div.learn-composition").each(function(idx) {
    var widget = this;
    var importance = $("div.sc", this).hide();
    $("div.labeled-radio-button", importance).hide();

    $("div.mc input", widget).click(function() {
      
      if ($("div.mc input:checked", widget).length > 1) {
        importance.show();
      } else {
        importance.hide();
      } 
      
      $("div.mc input:checked", widget).each(function(idx) {
        var jthis = $(this);
        var splitId = jthis.attr("id").split("-");
        var lastId = splitId[splitId.length - 1];
        var radio = $("div.sc input[value='" + lastId + "']", widget).parent().show();
      });

      $("div.mc input:not(:checked)", widget).each(function(idx) {
        var jthis = $(this);
        var splitId = jthis.attr("id").split("-");
        var lastId = splitId[splitId.length - 1];
        var radio = $("div.sc input[value='" + lastId + "']", widget).parent().hide();
      });});
  });

  $("div.election-choice").each(function(idx){
    var widget = this;
    $("div.vote-choice", widget).hide();

    $("div.did-vote input[value=yes]", this).click(function() {
      $("div.vote-choice", widget).slideDown();
    });

    $("div.did-vote input[value=no]", this).click(function() {
      $("div.vote-choice", widget).slideUp();
    });
  });

  // required questions
  // helper functions
  var allowContinue = function() {
    $("input[type=submit]").attr("disabled", false).removeClass("disabledButton");
    $("span.required").css("color", "green");
  }
  var denyContinue = function() {
    $("input[type=submit]").attr("disabled", "disabled").addClass("disabledButton");
    $("span.required").css("color", "red");
  }
  // hide and disable the continue button on pages with required items
  $("div.scribble-map, input#consent-consent").each(denyContinue);


  // Community maps:
  // Enable continue button when at least one polygon is drawn.

  $("div.scribble-map").bind("polygon-added", allowContinue);

  // either reseting the map or removing all polys causes the submit
  // to disable
  $("div.scribble-map .reset").click(denyContinue);

  $("div.scribble-map").bind("polygon-removed", function(e, npolys) {
    if (npolys == 0) {
      denyContinue();
    }
  });

  $("input#consent-consent").click(function() {
    if($(this).is(':checked')) {
      allowContinue();
    } else {
      denyContinue();
    }
  });

  // adding current position labels to the sliders
  $("div.slider").each(function() {
    var slider = $(this);
    var data = $("input.data", this);

    $("div.ui-slider", this).each(function() {
      var widget = $(this);
      var label = $("<div class = 'ui-slider-label'>0</div>");
      slider.append(label);
      widget.bind("slide", function(e, ui) {
        var delay = function() {
          label.html(ui.value).position
          ({
            my: 'center top',
            at: 'center bottom',
            of: ui.handle,
            offset: "0, 6"
          });
        };
        
        // wait for the ui.handle to set its position
        setTimeout(delay, 5);
      });
    });
  });
 

  // Address collection:
  // when the user supplies his/her address and it successfully
  // geocodes, enable the button.
  var addrSubmitQuery = "div#address ~ input[type=submit]";
 var addrButtonState = true; 

  var addressDirectContinue = function() {
    addrButtonState = true;
    $(addrSubmitQuery).unbind("click");
  }
  
  var addrClickCount = 0;
 
  var addressAskForInput = function() {
    if (addrButtonState) {
      
      addrButtonState = false;

      $(addrSubmitQuery).bind("click", function() {
        addrClickCount = addrClickCount + 1;
        if (addrClickCount > 5) {
          alert("Thank you for your participation. If you later decide to share your postal code or city information, feel free to try the survey again.")
          window.location.replace("/");
          return(false);
        } else {
          alert("This survey involves maps. If you are uncomfortable providing a postal code, could you please enter the name of your city or town?");
        }
 
        $("#address-address-finder-address").focus();
        return(false);
      });
    }
  }

  $("div.map-find-address").bind("geocode-response", function(e, status) {
    if (status) {
      addressDirectContinue();
    } else {
      addressAskForInput();
    }
  });

  // initial state is asking for postal code input.
  addressAskForInput();

  // fix for number #60: not getting proper focus on a form element
  $("#address-length-of-residence-x-years").click(function() { $("#address-x-years").focus();});

  // hiding employment follow ups appropriately
  
  var employmentFollowUps = $("div#canada-population div.employment-follow-up").hide();
  $("div#employed-student input").click(function() {
    if ($(this).val() == "unemployed") {
      employmentFollowUps.hide();
    } else {
      employmentFollowUps.show();
    }
  });
});

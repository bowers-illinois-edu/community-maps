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


/*!
 * Onine map survey demo
 * http://research.markmfredrickson.com
 * Requires the jQuery Java Script Library
 *
 * Copyright 2010, Mark Fredrickson
 * Dual licensed under the MIT or GPL Version 2 licenses, the
 * same license employed by jQuery:
 * http://jquery.org/license
 *
 */



var polygonControl;
var map;
var neighborhood;
var homeMarker; // name the home marker so we can grab it later
var homePoint;

$(document).ready(function() {
  map = new google.maps.Map($("#map_canvas")[0],
    {mapTypeId: google.maps.MapTypeId.ROADMAP,
      scrollwheel: false,
      zoom: 12});
  //map.setUIToDefault();

  neighborhood = []; // collection of polygons that forms the neighborhood/community
  // a is an array (e.g. neighborhood), p is a google.maps.Polygon to
  // make into an array and then remove from the map.
  var savePolygon = function(a, p, popupguard) {
    if (!popupguard) { popupguard = function() { return(true); }}
    var r = new google.maps.Polygon({map: map, paths: p.getPath().getArray()});
    p.setMap(null);
    a.push(r);
    var doAnyThing = true; // prevents multiple pop ups from appearing.
    google.maps.event.addListener(r, "click", function(e) {
      if (doAnyThing & popupguard() ) {
        doAnyThing = false;
        // Note: might be slightly more efficient to create the window
        // once, rather than for each click.
        var popup = new google.maps.InfoWindow({content: "", position: e.latLng});
        google.maps.event.addListener(popup, "closeclick", function() {
          doAnyThing = true;
        });
        var content = $("<div style = 'height: 6em'>").addClass("polygon-popup"); 
        content.append($("<h2>Do you want to delete this community?</h2>"));
        content.append($("<a class = 'fg-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only'>Yes</a>").click(function() {
          var idx = a.indexOf(r);
          if (idx != -1) { a.splice(idx, 1); }
          r.setMap(null);
          popup.close();
        }));
        content.append($("<a class = 'fg-button ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only'>No</a>").click(function() {
          popup.close();
          doAnyThing = true;
          return(false);
        }));
        popup.setContent(content[0]);
        popup.open(map);
      }
    });
  };

  var questions = $("#survey-questions").children();

  for(j = 0; j < questions.length; j++) {
    (function(i) {
      var current = $(questions[i]);
      if (i > 0) {
        // add previous link
        var link = $("<a class='fg-button ui-state-default fg-button-icon-left ui-corner-all prev-link' href='#'><span class='ui-icon ui-icon-circle-arrow-w'/>Previous</a>").click(function() {
          current.fadeOut("slow", function(){
            $(questions[i - 1]).fadeIn("slow"); 
          });
        });
        current.children().first().append(link);
      } 

      if (i < questions.length - 1) {
        var link = $("<a class='fg-button ui-state-default fg-button-icon-right ui-corner-all next-link' href='#'><span class='ui-icon ui-icon-circle-arrow-e'/>Next</a>").click(function() {
          current.fadeOut("slow", function(){
            $(questions[i + 1]).fadeIn("slow");
          });
        });

        current.children().first().append(link);
      }

    })(j); // work around for JS scoping issue with for loops
  }

  var link = $("<a class='fg-button ui-state-default fg-button-icon-right ui-corner-all next-link' href='#'><span class='ui-icon'/>Done</a>").click(function() {
    $("#last-question").hide();
    $("#show-data").show();
    var data = $("#thedata").serializeObject();
    var addressLatLng = homeMarker.getPosition();
    data.addressLat = addressLatLng.lat();
    data.addressLng = addressLatLng.lng();

    // in this next code block, the return values are wrapped into extra arrays 
    // this is because jQuery.map tries to flatten arrays as return values
    // I would argue this behavior is wrong, but it is what jQuery does, so we can
    // work around.
    data.paths = $.map(neighborhood, function(n, i) {
      var p = n.getPath().getArray();
      var stuff = $.map(p, function(e, j) {
        return([[e.lat(), e.lng()]]);
      });
      return([stuff]);
    });

    $("#show-data").append(prettyPrint(data, {maxDepth: 4}));

  });

  $("#last-question").children().first().append(link);

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

  questions.hide();

  var trainingLocation = new google.maps.LatLng(42.94, -122.10);
  var setupTraining = function() {
    // map.clearOverlays();
    map.setCenter(trainingLocation);
    map.setZoom(12);
  }

  setupTraining();

  var scribbler;
  var trainingRegions = [];
  $("#try-training").click(function() { 
    scribbler = scribbleOn(map, {mouseup: function(p) {
      savePolygon(trainingRegions, p)
    }});
    $(this).hide();
    $("#restart-training").show();
  });
  $("#restart-training").click(function() {
    setupTraining();
    $.each(trainingRegions, function(i, r) {
      r.setMap(null);
    });
    scribbleOff(scribbler);
    $("#try-training").show();
    $(this).hide();
  }).hide();
  

  $("#training-time-start").val((new Date().getTime()));
  $("#done-training").click(function() {
    $("#training-time-end").val((new Date().getTime()));
    
    scribbleOff(scribbler);
    // clear out the training regions
    $.each(trainingRegions, function(i, r) {
      r.setMap(null);
    });

    $("#training").fadeOut("slow", function() { 
      $("#geocode").fadeIn("slow", function() {
        $("#geocode-time-start").val((new Date().getTime()));
      }); 
    });     
  });

  // setting up the home marker
  var centerOnHome = function() {
    map.setOptions({center: homePoint, zoom: 16});
    homeMarker = new google.maps.Marker({
      map: map,
      position: homePoint,
      draggable: true
    });
  };

  // when the user is done, clikcing the button fixes the marker in place
  $("#done-moving").click(function() {
    homeMarker.setOptions({draggable: false});
    map.setOptions({center: homeMarker.getPosition(), zoom: Math.floor(Math.random() * 5) + 10});

    $("#geocode-time-end").val((new Date().getTime()));
    $("#geocode").fadeOut("slow", function() { 
      $("#draw-community").fadeIn("slow", function() {
        $("#draw-community-time-start").val((new Date()).getTime());
      }); 
    });     
  });

  // continuing from this screen is not an option until a valid point has been found
  $("#done-moving").hide();
  // make the "Find my location" button the result of hitting enter
  $("#address").keypress(function(e) {
    if(e.which == 13) {
      //$(this).blur();
      $('#address-update').click();
    }
  });

  $("#address-update").click(function() {
    // TODO lock the screen
    var address = $("#address").val();    
    var geocoder = new google.maps.Geocoder();
    
    geocoder.geocode({address: address}, function(gresult, gstatus) {

      if (gstatus != google.maps.GeocoderStatus.OK) {
        // TODO use diaglog for alerting the user it is not found
        alert("No location matching '" + address + "' found");
      } else {
        homePoint = gresult[0].geometry.location;
        if (homeMarker) {
          homeMarker.setMap(null);
        }
        $("#done-moving").show();
        centerOnHome();
      } 
    });
  });
  
  var allowRemoveCommunity = true;

  $("#done-drawing").hide();
 
  $("#add-community").click(function(){
    scribbler = scribbleOn(map, {mouseup: function(p) {
      savePolygon(neighborhood, p, function() { return(allowRemoveCommunity) });
      $("#done-drawing").show();
    }});
    $(this).hide();
    $("#restart-community").show();
  });

  $("#restart-community").click(function(){
    $.each(neighborhood, function(i, r) {
      r.setMap(null);
    });
    neighborhood = [];
    scribbleOff(scribbler);
    map.setOptions({center: homeMarker.getPosition(), zoom: Math.floor(Math.random() * 5) + 10});
    $(this).hide();
    $("#add-community").show();
  }).hide();

  $("#done-drawing").click(function(){
    $("#draw-community-time-end").val((new Date().getTime()));
    scribbleOff(scribbler);
    allowRemoveCommunity = false;
    $("#draw-community").fadeOut("slow", function() { questions.first().fadeIn("slow"); });     
  });

  // make clickable things have a hover state
  $(".fg-button").hover(function() { $(this).toggleClass("ui-state-hover"); });
});





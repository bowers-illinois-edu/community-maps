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
  map = new GMap2($("#map_canvas")[0]);
  map.setUIToDefault();
  map.disableScrollWheelZoom();
  
  neighborhood = []; // collection of polygons that forms the neighborhood/community
  
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

    // this is a little thorny. The GOverlay -> KML function is _asyncronous_.
    // Therefore, for each neighbor/community the user has specified, we have 
    // save the KML via a call back that also checks if all the data has arrived.
    // When all the data is available, it should output the data to screen. 
    // I will assume that no race conditions can happen on the kmlCompleted or 
    // kmlCount variables.
    var kmlCount = 0;
    var kmlCompleted = [];

    $.map(neighborhood, function(n) {
      n.getKml(function(kml) {
        kmlCount++;
        kmlCompleted.push(kml);
        if (kmlCount == neighborhood.length) {
          data.communities = kmlCompleted;
          $("#show-data").append(prettyPrint(data));
        }
      });
    });

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
  
 
  // region drawing function
  var addRegion = function(save) {
    var region = new GPolygon([], "#FF0000", 10, 1, "#ff1010", 0.1);
    
    if(save) {
      neighborhood.push(region);
    }

    map.addOverlay(region);
    //region.setFillStyle({color: "#0000FF", opacity: .5});
    region.enableDrawing();
  }

  var setupTraining = function() {

    var iconOptions = {};
    iconOptions.primaryColor = "#ee7700";
    iconOptions.strokeColor = "#000000";
    iconOptions.labelColor = "#000000";
    iconOptions.addStar = false;
    iconOptions.starPrimaryColor = "#FFFF00";
    iconOptions.starStrokeColor = "#0000FF";

    iconOptions.label = "1";
    var icon1 = MapIconMaker.createLabeledMarkerIcon(iconOptions);
    
    iconOptions.label = "2";
    var icon2 = MapIconMaker.createLabeledMarkerIcon(iconOptions);
    
        iconOptions.label = "3";
    var icon3 = MapIconMaker.createLabeledMarkerIcon(iconOptions);

    iconOptions.label = "4";
    var icon4 = MapIconMaker.createLabeledMarkerIcon(iconOptions);
    
    map.clearOverlays();
    map.setCenter(new GLatLng(42.94, -122.10), 12);
    
    var mopts = {clickable: false};

    // 12 o'clock
    map.addOverlay(new GMarker(new GLatLng(42.975, -122.10), icon1, true));
    // 9 o'clock
    map.addOverlay(new GMarker(new GLatLng(42.94, -122.165), icon2, true));
    // 6 o'clock
    map.addOverlay(new GMarker(new GLatLng(42.90, -122.10), icon3, true));
    // 3 o'clock
    map.addOverlay(new GMarker(new GLatLng(42.94, -122.055), icon4, true));
  }

  setupTraining();

  $("#try-training").click(function() { addRegion(false); });
  $("#restart-training").click(setupTraining);

  var centerOnHome = function() {
    map.setCenter(homePoint, 16);
    homeMarker = new GMarker(homePoint, {draggable: true});
    map.addOverlay(homeMarker);
  };

  $("#training-time-start").val((new Date().getTime()));
  $("#done-training").click(function() {
    $("#training-time-end").val((new Date().getTime()));
    $("#training").fadeOut("slow", function() { 
      $("#geocode").fadeIn("slow", function() {
        $("#geocode-time-start").val((new Date().getTime()));
      }); 
    });     
  });

  // setting up the home marker
  // when the user is done, clikcing the button fixes the marker in place
  $("#done-moving").click(function() {
     homeMarker.disableDragging(false);
     map.setCenter(homeMarker.getPoint(), Math.floor(Math.random() * 5) + 10);
     $("#geocode-time-end").val((new Date().getTime()));
    $("#geocode").fadeOut("slow", function() { $("#draw-community").fadeIn("slow"); });     
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
      var geocoder = new GClientGeocoder();
      var address = $("#address").val();
      geocoder.getLatLng(
        address,
        function(point) {
          if (!point) {
            // TODO use diaglog for alerting the user it is not found
            alert("No location matching '" + address + "' found");
          } else {
            homePoint = point;
            if (homeMarker) {
              map.removeOverlay(homeMarker);              
            }
            $("#done-moving").show();
            centerOnHome();
          }    
     });
  });


  $("#add-community").click(function(){
    addRegion(true);   
  })

  $("#restart-community").click(function(){
    for (i in neighborhood ) {
      map.removeOverlay(neighborhood[i]);
      neighborhood = [];
    }

     map.setCenter(homeMarker.getPoint(), Math.floor(Math.random() * 5) + 10);
  });

  $("#done-drawing").click(function(){
    homeMarker.disableDragging();
    $("#draw-community").fadeOut("slow", function() { questions.first().fadeIn("slow"); });     
  });

});

GEvent.addDomListener(window,"unload",function(){
	GUnload();
});



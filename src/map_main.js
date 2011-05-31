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
    {mapTypeId: google.maps.MapTypeId.ROADMAP});
  //map.setUIToDefault();
  
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
    }})(j); // work around for JS scoping issue with for loops
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

 questions.hide();
  
  // for the special block showing question, add a fake block group to the map.
  // the id is place on the question _before_ the block group
  // similarly, the block group question is responsible for hiding the group
  var block;
  $("#next-question-show-block-group a.next-link").click(function() {
    var mapBounds = map.getBounds();
    var ne = mapBounds.getNorthEast();
    var sw = mapBounds.getSouthWest();
    var width = Math.abs(ne.lng() - sw.lng());
    var height = Math.abs(ne.lat() - sw.lat());
    var a = new GLatLng(ne.lat() - .9 * height, ne.lng() - .9 * width);
    var b = new GLatLng(ne.lat() - .9 * height, sw.lng() + .9 * width);
    var c = new GLatLng(sw.lat() + .9 * height, ne.lng() - .9 * width);
    var d = new GLatLng(sw.lat() + .9 * height, sw.lng() + .9 * width);

    block = new GPolygon([a,b,d,c,a], "#0000FF", 5, .5, "#1010ff", .1);
    for (o in neighborhood) { map.removeOverlay(neighborhood[o]); }
    map.addOverlay(block);
  });

  $("#previous-question-hide-block-group a.prev-link").click(function() {
    for (o in neighborhood) { map.addOverlay(neighborhood[o]); }    
    map.removeOverlay(block);    
  })

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
    // map.clearOverlays();
    map.setCenter(new google.maps.LatLng(42.94, -122.10));
    map.setZoom(12);
    
    // 12 o'clock
    new google.maps.Marker({map: map, position: new google.maps.LatLng(42.975, -122.10), clickable: false});
    // 9 o'clock
    new google.maps.Marker({map: map, position: new google.maps.LatLng(42.94, -122.165), clickable: false});
    // 6 o'clock
    new google.maps.Marker({map: map, position: new google.maps.LatLng(42.90, -122.10),  clickable: false});
    // 3 o'clock
    new google.maps.Marker({map: map, position: new google.maps.LatLng(42.94, -122.055), clickable: false});
  }

  setupTraining();

  $("#try-training").click(function() { addRegion(false); });
  $("#restart-training").click(setupTraining);

  var centerOnHome = function() {
    map.setCenter(homePoint, 16);
    homeMarker = new GMarker(homePoint, {draggable: true});
    map.addOverlay(homeMarker);
  };

  $("#done-training").click(function() {
    $("#training").fadeOut("slow", function() { 
      centerOnHome();
      $("#move-marker").fadeIn("slow"); });     
  });

  // setting up the home marker
  // when the user is done, clikcing the button fixes the marker in place
  $("#done-moving").click(function() {
     homeMarker.disableDragging(false);
     map.setCenter(homeMarker.getPoint(), Math.floor(Math.random() * 5) + 10);
    $("#move-marker").fadeOut("slow", function() { $("#draw-community").fadeIn("slow"); });     
  });

  // the user can also try to update the map location with a new addr
  $("#address-update").click(function() {
      // TODO lock the screen
      var geocoder = new GClientGeocoder();
      var address = $("#geocoder-update").val();
      geocoder.getLatLng(
        address,
        function(point) {
          if (!point) {
            // TODO use diaglog for alerting the user it is not found
            alert(address + " not found");
          } else {
            homePoint = point;
            map.removeOverlay(homeMarker);
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
  })

  // TODO hide the check box to close the dialog (as it is not an option)
  // TODO hitting enter should submit the form like the lookup button
  // $("#geocoder-controls").dialog({
  //   width: 500,
  //   modal: true,
  //   draggable: false,
  //   resizeable: false,
  //   title: "Enter your address",
  //   buttons: {"Look up my address": function() {
  //     var dialog = this;
  //     var geocoder = new GClientGeocoder();
  //     var address = $("#geocoder").val();
  //     geocoder.getLatLng(
  //       address,
  //       function(point) {
  //         if (!point) {
  //           // TODO use diaglog for alerting the user it is not found
  //           alert(address + " not found");
  //         } else {
  //           homePoint = point;
  //           $(dialog).dialog("close");
  //           
  //         }    
  //       });
  //     }
  //   }
  // });
});





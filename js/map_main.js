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
  var popups = [];
  var savePolygon = function(map, a, p, popupguard, afterdelete) {
    if (!popupguard) { popupguard = function() { return(true); }}
    var r = new google.maps.Polygon({map: map, paths: p.getPath().getArray()});
    p.setMap(null);
    a.push(r);
    var popupmutex = true; // prevents multiple pop ups from appearing.
    google.maps.event.addListener(r, "click", function(e) {
      if (popupmutex & popupguard() ) {
        popupmutex = false;
        // Note: might be slightly more efficient to create the window
        // once, rather than for each click.
        var popup = new google.maps.InfoWindow({content: "", position: e.latLng});
        popups.push(popup);

        google.maps.event.addListener(popup, "closeclick", function() {
          popupmutex = true;
        });
        var content = $("<div style = 'height: 7em'>").addClass("polygon-popup"); 
        content.append($("<h2>Do you want to delete this community?</h2>"));
        content.append(makeButton("Yes").click(function() {
          var idx = a.indexOf(r);
          if (idx != -1) { a.splice(idx, 1); }
          r.setMap(null);
          if (afterdelete) { 
            afterdelete(r); 
          }
          popup.close();
        }));
        content.append(makeButton("No").click(function() {
          popup.close();
          popupmutex = true;
          return(false);
        }));
        popup.setContent(content[0]);
        popup.open(map);
      }
    });
  };

  $(".scribble-map").each(function(idx) {
    var data = [];
    
   
    var map = new google.maps.Map($(".map-canvas", this).get(0),
                                  {mapTypeId: google.maps.MapTypeId.ROADMAP,
                                   scrollwheel: false,
                                   zoom: 16});
                                 
    var center = new google.maps.LatLng($(".lat", this).val(), $(".lon", this).val());
    map.setOptions({center: center});

    var start = $(".start", this);
    var stop = $(".stop", this);
    var reset = $(".reset", this);
    var scribbler;
    
    start.click(function() {
      stop.show();
      reset.show();
      
      scribbler = scribbleOn(map, {mouseup: function(p) {
        savePolygon(map, data, p, function() { return(true) },
                    function() { if (data.length == 0) { stop.hide(); }}); 
      }});
      start.hide();
    });

    stop.click(function() {

    });
  });

  map = new google.maps.Map($("#map_canvas")[0],
    {mapTypeId: google.maps.MapTypeId.ROADMAP,
      scrollwheel: false,
      zoom: 12});
  //map.setUIToDefault();

  neighborhood = []; // collection of polygons that forms the neighborhood/community
  // a is an array (e.g. neighborhood), p is a google.maps.Polygon to
  // make into an array and then remove from the map.
  
  var popups = []; // similar to neighborhood but keeps track of popups on the screen.

  var savePolygon = function(a, p, popupguard, afterdelete) {
    if (!popupguard) { popupguard = function() { return(true); }}
    var r = new google.maps.Polygon({map: map, paths: p.getPath().getArray()});
    p.setMap(null);
    a.push(r);
    var popupmutex = true; // prevents multiple pop ups from appearing.
    google.maps.event.addListener(r, "click", function(e) {
      if (popupmutex & popupguard() ) {
        popupmutex = false;
        // Note: might be slightly more efficient to create the window
        // once, rather than for each click.
        var popup = new google.maps.InfoWindow({content: "", position: e.latLng});
        popups.push(popup);

        google.maps.event.addListener(popup, "closeclick", function() {
          popupmutex = true;
        });
        var content = $("<div style = 'height: 7em'>").addClass("polygon-popup"); 
        content.append($("<h2>Do you want to delete this community?</h2>"));
        content.append(makeButton("Yes").click(function() {
          var idx = a.indexOf(r);
          if (idx != -1) { a.splice(idx, 1); }
          r.setMap(null);
          if (afterdelete) { 
            afterdelete(r); 
          }
          popup.close();
        }));
        content.append(makeButton("No").click(function() {
          popup.close();
          popupmutex = true;
          return(false);
        }));
        popup.setContent(content[0]);
        popup.open(map);
      }
    });
  };


  
  // This should be a set from the user data, perhaps in a hidden
  // field
  var defaultLocation = new google.maps.LatLng(42.94, -122.10);
  homePoint = defaultLocation;

  var scribbler;

  // setting up the home marker
  var centerOnHome = function() {
    map.setOptions({center: homePoint, zoom: 16});
    homeMarker = new google.maps.Marker({
      map: map,
      position: homePoint,
      draggable: true
    });
  };

  // make the "Find my location" button the result of hitting enter
  // $("#address").keypress(function(e) {
  //   if(e.which == 13) {
  //     //$(this).blur();
  //     $('#address-update').click();
  //   }
  // });

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
      savePolygon(neighborhood, p, function() { return(allowRemoveCommunity) },
                 function() { if (neighborhood.length == 0) { $("#done-drawing").hide() }});          
      $("#done-drawing").show();
    }});
    $(this).hide();
    $("#restart-community").show();
  });

  $("#restart-community").click(function(){
    $.each(neighborhood, function(i, r) {
      r.setMap(null);
    });
    
    $.each(popups, function(i, p) {
      p.setMap(null);
    });

    neighborhood = [];
    popups = [];

    scribbleOff(scribbler);
    map.setOptions({center: homeMarker.getPosition(), zoom: Math.floor(Math.random() * 5) + 10});
    $(this).hide();
    $("#add-community").show();
  }).hide();

  $("#done-drawing").click(function(){
    $("#draw-community-time-end").val((new Date().getTime()));

    $.each(popups, function(i, p) {
      p.setMap(null);
    });

    scribbleOff(scribbler);
    allowRemoveCommunity = false;
    $("#draw-community").fadeOut("slow", function() { $("#survey-questions").children().first().fadeIn("slow"); });     
  });

  // make clickable things have a hover state
  $(".fg-button").hover(function() { $(this).toggleClass("ui-state-hover"); });
});





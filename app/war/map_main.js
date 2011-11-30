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
  // polygon defaults
  var polycolor = "#AA0000"; // semi-transparent red
  var fillopacity = 0.6;
  var strokeweight = 1;
 
  var popups = [];
  var savePolygon = function(map, datafield, a, p, popupguard, afterdelete) {
    if (!popupguard) { popupguard = function() { return(true); }}
    var r = new google.maps.Polygon({map: map, paths: p.getPath().getArray()});
    p.setMap(null);
    a.push(r);
    
    var savePaths = function() {
      // data format is lat,lon,lat,lon,... ; lat,lon,lat,lon,... ; ...
      datafield.val($.map(a, function(n, i) {
        var p = n.getPath().getArray();
        var stuff = $.map(p, function(e, j) {
          return([[e.lat(), e.lng()]]);
        });
        return(stuff.join(","));
      }).join(";"));
    }
    
    savePaths(); // save on write

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
          var idx = $.inArray(r, a);
          if (idx != -1) { a.splice(idx, 1); }
          r.setMap(null);
          if (afterdelete) { 
            afterdelete(r); 
          }
          savePaths(); // update the data field
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

  // record map events
  // map is a Google Map object,
  // hiddenField is an input into which events are appended
  var mapRecorder = function(map, hiddenField) {
    var f = $(hiddenField);
    f.val("(Started," + (new Date().getTime()) + ")");
    var record = function(msg) {
      f.val(f.val() + ";(" + msg + "," + (new Date().getTime()) + ")");
    };

    // records zoom, moving
    google.maps.event.addListener(map, 
                                  "zoom_changed", 
                                  function() { record("zoom:" + map.getZoom())});

    google.maps.event.addListener(map,
                                  "center_changed",
                                   function() { record("center:" + map.getCenter().toString())});
  }

  $(".scribble-map").each(function(idx) {
    var widget = $(this);

    var data = [];

    // NOTE: when this gets refactored into a plugin, the map options
    // should be an argument to the function
    var map = new google.maps.Map($(".map-canvas", this).get(0),
                                  {mapTypeId: google.maps.MapTypeId.ROADMAP,
                                   scrollwheel: false,
                                   maxZoom: 17,
                                   zoom: 12,
                                   minZoom: 4,
                                   streetViewControl: false});

    
                                 
    var center = new google.maps.LatLng($(".lat", this).val(), $(".lon", this).val());
    map.setOptions({center: center});
    this.gmap = map;

    // record user interations
    mapRecorder(map, $("input.events", this))

    var start = $(".start", this);
    var stop = $(".stop", this);
    var reset = $(".reset", this);
    var hiddenfield = $(".map-data", this);

    var scribbler;
    
    start.click(function() {
      stop.show();
      reset.show();
      
      scribbler = scribbleOn(map, {mouseup: function(p) {
        savePolygon(map, 
                    hiddenfield, 
                    data, 
                    p, 
                    function() { return(true) },
                    function() { 
                      widget.trigger("polygon-removed", [data.length]);
                    }); 
        widget.trigger("polygon-added");
      }});
      start.hide();
    });

    stop.click(function() {
      scribbleOff(scribbler);
      stop.hide();
      start.show();
      reset.hide();
    });

    reset.click(function() {
      hiddenfield.val("");
      $.map(data, function(item, idx) {
        item.setMap(null);
      });
      data = [];
    });});

  // make the "Find my location" button the result of hitting enter
  // $("#address").keypress(function(e) {
  //   if(e.which == 13) {
  //     //$(this).blur();
  //     $('#address-update').click();
  //   }
  // });

  // $("#address-update").click(function() {
  //   // TODO lock the screen
  //   var address = $("#address").val();    
  //   var geocoder = new google.maps.Geocoder();
  //   
  //   geocoder.geocode({address: address}, function(gresult, gstatus) {
  //  
  //     if (gstatus != google.maps.GeocoderStatus.OK) {
  //       // TODO use diaglog for alerting the user it is not found
  //       alert("No location matching '" + address + "' found");
  //     } else {
  //       homePoint = gresult[0].geometry.location;
  //       if (homeMarker) {
  //         homeMarker.setMap(null);
  //       }
  //       $("#done-moving").show();
  //       centerOnHome();
  //     } 
  //   });
  // });
  var staticMap = function(within, center) {
    return(new google.maps.Map($("div.map-canvas", within).get(0),{
      center: center,
      zoom: 8,
      mapTypeId: google.maps.MapTypeId.ROADMAP,
      draggable: false,
      disableDefaultUI: true,
      disableDoubleClickZoom: true,
      scrollwheel: false
    }));
  }

  // Static mapping with supplied polygons in hidden fields
  $("div.static-map").each(function(idx) {
    
    var bounds = new google.maps.LatLngBounds();
    var polygons = $("input.polygon", this).map(function(idx) {
      var polystr = $(this).val();
      polystr = polystr.split(";")
      var latlngs = $.map(polystr, function(pair, idx) {
        var tmp = pair.split(",");
        var ll = new google.maps.LatLng(tmp[0], tmp[1]);
        bounds.extend(ll);
        return(ll);
      });
      return(new google.maps.Polygon({paths: latlngs, 
                                      fillColor: polycolor,
                                      fillOpacity: fillopacity,
                                      strokeColor: polycolor,
                                      strokeWeight: strokeweight}));

    }).get(); // turn into a regular array for convenience

    var map = staticMap(this, bounds.getCenter());

    map.fitBounds(bounds);
    $.each(polygons, function(idx, p) {
      p.setMap(map);
    });
  });

  // displaying kml from URL in a hidden field
  $("div.kml-map").each(function(idx) {
    var url = $("input.url", this).val();
    var map = staticMap(this, new google.maps.LatLng(48.25, -52.96));
    var klayer = new google.maps.KmlLayer(url, {map: map});
    google.maps.event.trigger(map, "resize");
 
  });
});

 

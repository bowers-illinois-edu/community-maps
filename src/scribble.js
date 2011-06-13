/*******************************************************************************
 * Scribble on Google Maps
 * 
 * (c) 2011 Mark M. Fredrickson
 *
 * Requires jQuery and Google Maps (v3) to be loaded already
 *
 * Example Usage:
 *  
 *  $("#my-button").click(function() {
 *    scribbleOn(myMap)
 *  });
 *
 ******************************************************************************/

scribbleOn = function(map, options) {
  defaults = {
    interval: 25, // mouse position sampling interval
    mouseup: null // function to be run when mouse button comes up
  }

  var opts = $.extend({}, defaults, options);
  var jmap = $(map.getDiv());

  var drawing = false;
  var p; // will hold the current polyline
  var currentListener;
  var currentTimeOut;

  var scribbler = google.maps.event.addListener(map, 'mousedown', function(e) {

    map.setOptions({draggable: false});

    drawing = false;  
    p = new google.maps.Polyline({
      map: map,
      path: [e.latLng]
    });

    currentListener = google.maps.event.addListener(map, 'mousemove', function(e) {
      if (drawing) {
        var path = p.getPath();
        path.push(e.latLng); // pushing to path automatically updates the line
        drawing = false;
      }
    });

    var loop = function() {
      drawing = true;
      currentTimeOut = setTimeout(loop, opts.interval);
    }

    currentTimeOut = setTimeout(loop, opts.interval); 

  });

  jmap.mouseup(function() {
    if (drawing) {

      drawing = false;
      google.maps.event.removeListener(currentListener);       
      map.setOptions({draggable: true });
      clearTimeout(currentTimeOut);

      if (opts.mouseup) {
        opts.mouseup(p);
      }
    }
  });

  return(scribbler)
}

scribbleOff = function(scribbleListener) {
  google.maps.event.removeListener(scribbleListener);
}


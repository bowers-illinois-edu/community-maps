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
  var defaults = {
    interval: 25, // mouse position sampling interval
    mouseup: null, // function to be run when mouse button comes up
    wait: 3000 // milliseconds to wait before considering mouse to be final
  };

  var opts = $.extend({}, defaults, options);
  var jmap = $(map.getDiv());

  var drawing = false;
  var waiting = false;
  var waitingTimeOut;
  var waitingFunction;

  var p; // will hold the current polyline
  var currentListener;
  var currentTimeOut;

  map.setOptions({draggableCursor: "crosshair"});

  var scribbler = google.maps.event.addListener(map, 'mousedown', function(e) {
    jmap.addClass("drawing");
    map.setOptions({draggable: false});

    drawing = false;
    p = new google.maps.Polyline({
      map: map,
      path: [e.latLng]
    });

    currentListener = google.maps.event.addListener(map, 'mousemove', function(e) {
      if (waiting) {
        waiting = false;
        clearTimeout(waitingTimeOut);
        waitingFunction();
        return(false);
      }

      if (drawing & !waiting) {
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

    google.maps.event.addListener(p, "mousedown", function(e) {
      if (waiting) {
        map.setOptions({draggable: false});
        clearTimeout(waitingTimeOut);
        waiting = false;
      }
    });

  });

  jmap.mouseup(function() {
    if (drawing) {
      // to get map clicks we need draggable == true
      map.setOptions({draggable: true});
      waiting = true;
      waitingFunction = function() {
        drawing = false;
        waiting = false;
        jmap.removeClass("drawing");

        google.maps.event.removeListener(currentListener);       
        map.setOptions({draggable: true });
        clearTimeout(currentTimeOut);

        if (opts.mouseup) {
          opts.mouseup(p);
        }
      }
      waitingTimeOut = setTimeout(waitingFunction, opts.wait);
    }
  });

  return({scribbler: scribbler, map: map})
}

scribbleOff = function(scribbleObj) {
  if(scribbleObj.scribbler) {
    scribbleObj.map.setOptions({draggableCursor: null});
    google.maps.event.removeListener(scribbleObj.scribbler);
  }
}


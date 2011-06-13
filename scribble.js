scribble = function(map, options) {
  defaults = {
    interval: 25,
    mouseup: null
  }

  var opts = $.extend({}, defaults, options);
  var jmap = $(map.getDiv());

  var drawing = false;
  var p; // will hold the current polyline
  var currentListener;
  var currentTimeOut;

  google.maps.event.addListener(map, 'mousedown', function(e) {
    
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

}

$(document).ready(function() {

  var myLatlng = new google.maps.LatLng(40, -88);
  var myOptions = {
    zoom: 8,
    center: myLatlng,
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    disableDoubleClickZoom: true,
    scrollwheel: false
  };
  
  map = new google.maps.Map(document.getElementById("map_canvas"), myOptions); 
  regions = [];

  scribble(map, {mouseup: function(p) {
      var r = new google.maps.Polygon({map: map, paths: p.getPath().getArray()});
      p.setMap(null);
      regions.push(r);
  }});

});


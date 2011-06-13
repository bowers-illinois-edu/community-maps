$(document).ready(function() {
  var SPEED = 1000;

  var myLatlng = new google.maps.LatLng(40, -88);
  var myOptions = {
    zoom: 8,
    center: myLatlng,
    mapTypeId: google.maps.MapTypeId.ROADMAP,
  };

  map = new google.maps.Map(document.getElementById("map_canvas"), myOptions); 
  
  var drawing = false;
  scribbles = [];
  var currentListener;
  var currentTimeOut;

  //var clear = function() { 
  //  clearTimeout(currentTimeOut);
  //  google.maps.event.removeListener(currentListener);
  //};
  
  google.maps.event.addListener(map, 'click', function(e) {
    
      map.setOptions({draggable: false});

      drawing = true;  
      var p = new google.maps.Polyline({
        map: map,
        path: [e.latLng]
      });

      scribbles.push(p);
      
      currentListener = google.maps.event.addListener(map, 'mousemove', function(e) {
        var path = p.getPath();
        path.push(e.latLng); // pushing to path automatically updates the line
      });

      google.maps.event.addListener(p, 'click', function(e) {
        drawing = false;
        google.maps.event.removeListener(currentListener);       
        map.setOptions({draggable: true });
      });
  });
    
  // google.maps.event.addListener(map, 'mouseup', function() {
  //   console.log("mouse up");
  //   drawing = false; 
  //   if (currentListener) {
  //     google.maps.event.removeListener(currentListener);
  //   }
  // });

});


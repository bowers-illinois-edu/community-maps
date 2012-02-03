// Use a map to locate R's address

$(document).ready(function() {

  $(".map-find-address").each(function(idx) {
    var widget = $(this);

    var zoomLevel = 16;

    var mapDiv = $("div.map-canvas", this);
    var map = new google.maps.Map(mapDiv.get(0),
                                  {mapTypeId: google.maps.MapTypeId.ROADMAP,
                                   scrollwheel: false,
                                   zoom: zoomLevel});

    mapDiv.hide();
    var addressField = $("input.address", this);
    var updateButton = $("a.update", this);
    
    var point = null;
    var latlngField = $("input.latlng", this);

    var marker = new google.maps.Marker({
      map: map,
      clickable: false,
      position: new google.maps.LatLng(43.652527,-79.381961)}); //toronto

    // make the "Find my location" button the result of hitting enter
    addressField.keypress(function(e) {
      if(e.which == 13) {
        //$(this).blur();
        updateButton.click();
        return(false);
      }
    });

    updateButton.click(function() {
      // TODO lock the screen
      var address = addressField.val();    
      var geocoder = new google.maps.Geocoder();
      
      geocoder.geocode({address: address}, function(gresult, gstatus) {
        
        if (gstatus != google.maps.GeocoderStatus.OK) {
          // TODO use diaglog for alerting the user it is not found
          modal("No location matching '" + address + "' found. Please enter your postal code or address again.");
          widget.trigger("geocode-response", [false]); // geocode
          // unsuccessful
        } else {
          mapDiv.slideDown(function() {
            // set the map to the right point
            google.maps.event.trigger(map, "resize");
            point = gresult[0].geometry.location;
            marker.setPosition(point);
            map.setCenter(point);
            map.setOptions({zoom: zoomLevel});

            // save the data
            latlngField.val([point.lat(), point.lng()].join(","));
            widget.trigger("geocode-response", [true]);
          });

        } 
      });
    });
  }); 
}); 

// Use a map to locate R's address

$(document).ready(function() {
  var enteredValidAddress = false;
  var geocode = function(fail, success) {
    var address = $("#address-address-finder-address").val();    
    var geocoder = new google.maps.Geocoder();
    
    geocoder.geocode({address: address, region: "ca" }, function(gresult, gstatus) {
      
      if (gstatus != google.maps.GeocoderStatus.OK) {
        // TODO use diaglog for alerting the user it is not found
        if (fail) {
          fail();
        }
        enteredValidAddress = false;
      } else {
        var minlon = -141.018073107509; var maxlon = -52.5822958601443;
        var minlat = 41.6769493195857; var maxlat = 89.9994270756251;
        var point = gresult[0].geometry.location;

        if (point.lat() < minlat || point.lat() > maxlat || point.lng() < minlon || point.lng() > maxlon) {
          fail();
          enteredValidAddress = false;
        } else {
          enteredValidAddress = true;
          // save the data
          $("input.latlng").val([point.lat(), point.lng()].join(","));
          if(success) {
            success(gresult);
          }
        }
      } 
    });
  };

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
      var fail = function() {
          modal("Aucun lieu au Canada ne correspond au code postal ou à l'adresse que vous avez fourni. Insérez le code postal ou l'adresse encore une fois svp.");
      };

      geocode(fail, function(gresult) {
        mapDiv.slideDown(function() {
          // set the map to the right point
          google.maps.event.trigger(map, "resize");
          point = gresult[0].geometry.location;
          marker.setPosition(point);
          map.setCenter(point);
          map.setOptions({zoom: zoomLevel});
      
        });
      });
    });
  }); 

    // Address collection:
  // when the user supplies his/her address and it successfully
  // geocodes, enable the button.
  var addrClickCount = 0;
 
  $("div#address ~ input[type=submit]").click(function() {
    if (enteredValidAddress) {
      return(true);
    }

    var fail = function() {
      addrClickCount = addrClickCount + 1;
      if (addrClickCount > 5) {
        // using alert instead of modal() because I don't want to
        // bump until after a "ok" click, and I don't want to make
        // modal any more complicated.
        alert("Merci de votre participation. Si vous décidez plus tard de partager votre code postal ou votre municipalité de résidence, n'hésitez pas à compléter le questionnaire de nouveau.")
        window.location.replace("/");
      } else {
        $("#address-address-finder-address").focus();
        modal("Cette enquête nécessite l'utilisation de cartes. Si vous êtes incomfortable avec l'idée de partager votre code postal, pourriez-vous fournir le nom de votre municipalité?");
      }
    }
    
    var success = function() { $("form").submit() };
    geocode(fail, success);

    return(false);
  });

}); 

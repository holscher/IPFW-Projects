<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
<title>Mobile News Map</title>

<script type="text/javascript"
  src="http://ecn.dev.virtualearth.net/mapcontrol/mapcontrol.ashx?v=7.0"></script>
<script src="${pageContext.request.contextPath}/rest-js" type="text/javascript" ></script>
<script type="text/javascript" src="gears_init.js"></script>
</head>
<body>
<h1 class="title">News @</h1>
<div class="location" id="location">Determining Current Location</div>
  <div id="mapDiv"
    style="position: relative; width: 800px; height: 600px;"></div>
  <div id="info"></div>
<script type="text/javascript">
if (!Number.toFixed) {
  Number.prototype.toFixed=function(x) {
  var temp=this;
  temp=Math.round(temp*Math.pow(10,x))/Math.pow(10,x);
  return temp;
  };
}
window.onload=startLocation;
var gl = null;
var METERS_TO_MILES = 0.000621371192;
var map = null;


  function initMap(position) {
    map = new Microsoft.Maps.Map(
        document.getElementById("mapDiv"),
        {
          credentials : "AsLi4vfAcChAMuLFL36XOUceNXaxrv7M6KsQWNACOmMj8zt9Zlzw9q8GYCiAWI2y",
          center : new Microsoft.Maps.Location(position.coords.latitude, position.coords.longitude),
          mapTypeId : Microsoft.Maps.MapTypeId.road,
          zoom : 7
        });
  }

  function displayPosition(position) {
    var mapDiv = document.getElementById("mapDiv");
    if (mapDiv) {
      if (!map) {
        initMap(position);
      }
      showNews(position);
    }
    var location = document.getElementById("location");
    if (location) {
      addr = Geocode.getAddress({lon:position.coords.longitude, lat:position.coords.latitude});
      if (addr) {
        location.innerHTML = addr;
      } else {
        location.innerHTML = "Unable to determine address.<br>Latitude: " + position.coords.latitude +
            " Longitude: " + position.coords.longitude;
            
      }
    }
  }

  function displayError(positionError) {
    alert("Error in geolocation service.");
  }

  function startLocation() {
    try {
      if (typeof (navigator.geolocation) == 'undefined') {
        // Make sure we have Gears. If not, tell the user.
        if (!window.google || !google.gears) {
          if (confirm("This demo requires Gears to be installed. Install now?")) {
            // Use an absolute URL to allow this to work when run from a local file.
            location.href = "http://code.google.com/apis/gears/install.html";
            return;
          }
        }
        gl = google.gears.factory.create('beta.geolocation');
      } else {
        gl = navigator.geolocation;
      }
    } catch (e) {
    }
    if (gl) {
      gl.watchPosition(displayPosition, displayError, {
        maximumAge : 43200000
      });
    } else {
      document.getElementById("location").innerHTML = "Sorry, but geolocation services are not supported by your browser.";
    }
  }

  function showNews(position) {
    var options = map.getOptions();

    // Set the zoom level of the map
    options.center = new Microsoft.Maps.Location(position.coords.latitude, position.coords.longitude);
    map.setView(options);

    var newsNear = ArticlesLocal.getNeighborsWithin({
      lon : position.coords.longitude,
      lat : position.coords.latitude
    });
    var articles = newsNear.matches;
    var info = document.getElementById("info");
    if (!articles || articles.length == 0) {
      if (newsNear.distance) {
        info.innerHTML = "There are no articles within "
            + new Number(newsNear.distance * METERS_TO_MILES).toFixed(3)
            + " miles.";
      } else {
        info.innerHTML = "There were no matching articles found.";
      }
    } else {
      info.innerHTML = "Displaying " + articles.length + " articles.";

      for ( var i = 0; i < articles.length; i++) {
        // Add a pin to the center of the map
        pinLoc = new Microsoft.Maps.Location(articles[i].latitude, articles[i].longitude);
        var pin = new Microsoft.Maps.Pushpin(pinLoc, {text: '' + (1 + i)}); 
        var myurl = articles[i].url;

        // Create the info box for the pushpin
        var pinInfobox = new Microsoft.Maps.Infobox(pinLoc, 
            {title: articles[i].title,
             visible: false,
             titleClickHandler: new Function('location.href = "' + myurl + '"'),
             offset: new Microsoft.Maps.Point(0,15)});
        pin.pinInfobox = pinInfobox;

        // Add handler for the pushpin click event.
        Microsoft.Maps.Events.addHandler(pin, 'click', function(e) {
          e.target.pinInfobox.setOptions({ visible:true });
          });

        // Add the pushpin and info box to the map
        map.entities.push(pin);
        map.entities.push(pinInfobox);
      }
    }
  }
</script>
<div class='return'><a href="news.jsp">Back News List</a></div>
</body>
</html>

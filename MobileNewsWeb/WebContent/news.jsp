<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
<title>Mobile News</title>
<link rel="stylesheet" href="news.css" />
<script src="${pageContext.request.contextPath}/rest-js" type="text/javascript" ></script>
<script type="text/javascript" src="gears_init.js"></script>
</head>
<body>
<h1 class="title">News @</h1>
<div class="location" id="location">Determining Current Location</div>
<div class="news" id="news"></div>

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
var DAYS = 24 * 60 * 60;
 
function displayPosition(position) {
  var location = document.getElementById("location");
  if (location) {
    addr = Geocode.getAddress({lon:position.coords.longitude, lat:position.coords.latitude});
    if (addr) {
      location.innerHTML = addr;
    } else {
      location.innerHTML = "Unable to determine address.<br>Latitude: " + position.coords.latitude +
          " Longitude: " + position.coords.longitude;
          
    }
    showNews(position);
  }
}
 
function displayError(positionError) {
  alert("Error in geolocation service.");
}
 
function startLocation() {
  try {
    if(typeof(navigator.geolocation) == 'undefined'){
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
  }catch(e){}
  if (gl) {
    gl.watchPosition(displayPosition, displayError, {maximumAge:43200000});
  } else {
    document.getElementById("location").innerHTML = "Sorry, but geolocation services are not supported by your browser.";  
  }
}
function timeDiff(now, past) {
  var diff = (now - past) / 1000;
  var str = "";
  var days = Math.floor(diff / DAYS);
  if (days != 0) {
    str += days + "d ";
  }  
  var minutes = Math.floor((diff % DAYS) / 3600);
  if (days != 0 || minutes != 0) {
    str += minutes + "m ";
  }
  var seconds = Math.floor(diff % 60);
  return str + seconds + "s";
}

function showNews(position) {
  var newsNear = ArticlesLocal.getNeighborsWithin({lon:position.coords.longitude, lat:position.coords.latitude});
  var articles = newsNear.matches;
  var news = document.getElementById("news");
  if (!articles || articles.length == 0) {
    if (newsNear.distance) {
      news.innerHTML = "There are no articles within " + new Number(newsNear.distance * METERS_TO_MILES).toFixed(3) + " miles.";
    } else {
      news.innerHTML = "There were no matching articles found.";
    }
  } else {
    var now = new Date().getTime();
    var newsHtml = "<table class='news_table' cellspacing='0' border='1'><thead><tr><td>Title</td><td>Distance (Miles)</td><td>Location</td><td>Published</td></tr></thead>";
    for (var i = 0; i < articles.length; i++) {
      newsHtml += "<tr><td><a href=\"" + articles[i].url + "\">" + articles[i].title + "</a></td><td>"
        + new Number(articles[i].distance * METERS_TO_MILES).toFixed(3) + "</td><td>" + articles[i].where + "</td><td>" +
        timeDiff(now, articles[i].lastPublished) + "</td></tr>";
    }
    newsHtml += "</table>";
    news.innerHTML = newsHtml;
  }
}
</script>
<div class='return'><a href="map.jsp">Go to Map</a></div>

</body>
</html>

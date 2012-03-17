<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
<title>Mobile News</title>
<link rel="stylesheet" href="news.css" />
<script src="${pageContext.request.contextPath}/rest-js" type="text/javascript" ></script>
<script type="text/javascript" src="gears_init.js"></script>
</head>
<body onclick="hideVisible()">
<h1 class="title">Stories @</h1>
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
var visibleTable;
 
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
  var newsNear = StoriesLocal.getNeighborsWithin({lon:position.coords.longitude, lat:position.coords.latitude, max:100});
  var articles = newsNear.matches;
  var news = document.getElementById("news");
  if (!articles || articles.length == 0) {
    if (newsNear.distance) {
      news.innerHTML = "There are no articles within " + new Number(newsNear.distance * METERS_TO_MILES).toFixed(3) + " miles.";
    } else {
      news.innerHTML = "There were no matching articles found.";
    }
  } else {
    news.innerHTML = formatNewsTable('Story', articles, 'stories', 'stories');
  }
}

function showArticles(articles) {
  var atable = document.getElementById(articles);
  hideVisible();
  atable.style.visibility = 'visible';
  visibleTable = atable;
}

function hideVisible() {
  if (visibleTable != null)
    visibleTable.style.visibility = 'hidden';
}
function formatNewsTable(column1, articles, cssClass, tableId) {
  var newsHtml = "<table class='" + cssClass + "' id='" + tableId + "' cellspacing='0' border='1'><thead><tr><td>" + column1 + "</td><td>Distance (Miles)</td><td>Location</td><td>Last Published</td></tr></thead>";
  var now = new Date().getTime();
  for (var i = 0; i < articles.length; i++) {
    if (articles[i].url == null || articles[i].url == 'null') {
      var showFunc = "showArticles(\"articles" + i + "\")";
      newsHtml += "<tr><td><a onmouseover=" + showFunc + " href='javascript:" + showFunc + "'>" + articles[i].title + "</a>" +
      formatNewsTable('Articles like ' + articles[i].title, articles[i].children, 'articles', 'articles' + i) +
      "</td>";
    } else {
      newsHtml += "<tr><td><a href=\"" + articles[i].url + "\">" + articles[i].title + "</a></td>";
    }
    newsHtml += "<td>"
      + new Number(articles[i].distance * METERS_TO_MILES).toFixed(3) + "</td><td>" + articles[i].where + "</td><td align='right'>" +
      timeDiff(now, articles[i].lastPublished) + " ago</td></tr>";
  }
  return newsHtml + "</table>";
 
}
</script>
<div class='return'><a href="storymap.jsp">Go to Map</a></div>

</body>
</html>

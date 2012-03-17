package edu.ipfw.cs575.mobilenews.services.data;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NewsMatch {

  private String title;
  private String url;
  private String where;
  private double distance;
  private double longitude;
  private double latitude;
  private long lastPublished;
  private List<NewsMatch>children;

  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public double getDistance() {
    return distance;
  }
  public void setDistance(double distance) {
    this.distance = distance;
  }
  public double getLongitude() {
    return longitude;
  }
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
  public double getLatitude() {
    return latitude;
  }
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }
  public String getWhere() {
    return where;
  }
  public void setWhere(String where) {
    this.where = where;
  }
  public long getLastPublished() {
    return lastPublished;
  }
  public void setLastPublished(long lastPublished) {
    this.lastPublished = lastPublished;
  }
  
  public List<NewsMatch>getChildren() {
    return children;
  }
  public void setChildren(List<NewsMatch> children) {
    this.children = children;
  }
}

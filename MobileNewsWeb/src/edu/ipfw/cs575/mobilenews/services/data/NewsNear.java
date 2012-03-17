package edu.ipfw.cs575.mobilenews.services.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NewsNear {

  private String where;
  private double longitude;
  private double latitude;
  private Double distance;
  private Integer nearestNeighbors;
  private List<NewsMatch> matches;

  public List<NewsMatch> getMatches() {
    if (matches == null) {
      matches = new ArrayList<NewsMatch>();
    }
    return matches;
  }

  public String getWhere() {
    return where;
  }

  public void setWhere(String where) {
    this.where = where;
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

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public Integer getNearestNeighbors() {
    return nearestNeighbors;
  }

  public void setNearestNeighbors(Integer nearestNeighbors) {
    this.nearestNeighbors = nearestNeighbors;
  }

}

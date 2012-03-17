package edu.ipfw.cs575.mobilenews.data;

import java.util.Collection;

import com.vividsolutions.jts.geom.Point;

public interface GeotaggedItem {

  String getTitle();

  Point getLocation();

  String getWhere();

  long getLastPublished();

  Collection<? extends GeotaggedItem>getChildren();

  String getUrl();
}

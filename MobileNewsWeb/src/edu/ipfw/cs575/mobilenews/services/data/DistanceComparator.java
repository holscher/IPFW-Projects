package edu.ipfw.cs575.mobilenews.services.data;

import java.util.Comparator;

public class DistanceComparator implements Comparator<NewsMatch> {

  @Override
  public int compare(NewsMatch o1, NewsMatch o2) {
    double d1 = o1.getDistance(), d2 = o2.getDistance();
    if (d1 > d2) {
      return 1;
    } else if (d1 < d2) {
      return -1;
    } else {
      return 0;
    }
  }

}

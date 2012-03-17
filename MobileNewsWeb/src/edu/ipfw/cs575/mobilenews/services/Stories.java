package edu.ipfw.cs575.mobilenews.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernatespatial.oracle.criterion.OracleSpatialRestrictions;
import org.hibernatespatial.oracle.criterion.SDOParameterMap;
import org.opengis.referencing.operation.TransformException;

import com.ipfw.cs575.mobilenews.util.Geocoder;
import com.ipfw.cs575.mobilenews.util.GeometryUtils;
import com.vividsolutions.jts.geom.Point;

import edu.ipfw.cs575.mobilenews.data.Cluster;
import edu.ipfw.cs575.mobilenews.data.GeotaggedItem;
import edu.ipfw.cs575.mobilenews.services.data.DistanceComparator;
import edu.ipfw.cs575.mobilenews.services.data.NewsMatch;
import edu.ipfw.cs575.mobilenews.services.data.NewsNear;

/**
 * Implementation of JAX-RS service
 * that gets story clusters.
 */
@Stateless
public class Stories implements StoriesLocal {
  private static final Logger log = Logger.getLogger(Stories.class);
/*
 * This could probably share some code
 * with ArticlesLocal but I don't have time
 * to figure out the best way since they are
 * both EJBs.
 */
  @PersistenceContext(unitName = "MobileNews")
  EntityManager em;

  /* (non-Javadoc)
   * @see edu.ipfw.cs575.mobilenews.services.ArticlesLocal#getNear(double, double, double)
   */
  @Override
  public NewsNear getNeighborsWithin(double longitude, double latitude, int maxNeighbors, int maxAgeDays) {
    NewsNear result = new NewsNear();
    result.setNearestNeighbors(maxNeighbors);
    result.setLatitude(latitude);
    result.setLongitude(longitude);
    result.setWhere(Geocoder.reverseGeocodeToName(longitude, latitude));
    /*
     * To use Hibernate spatial we have to drop out of JPA and use Hibernate
     * specific classes..
     */
    SDOParameterMap sdoParameterMap = new SDOParameterMap();
    Point p = GeometryUtils.createPoint(longitude, latitude);
    Criteria criteria = em.unwrap(Session.class).createCriteria(Cluster.class);
    long earliestPubDate = System.currentTimeMillis() - maxAgeDays * 24 * 60 * 60 * 1000;
    criteria.add(Restrictions.ge("lastPublished", earliestPubDate));
    criteria.add(Restrictions.sqlRestriction("rownum <= ?", maxNeighbors, IntegerType.INSTANCE));
    criteria.add(OracleSpatialRestrictions.SDONN("location", p, sdoParameterMap));
    extractResults(p, result.getMatches(), criteria.list(), maxNeighbors);
    return result;
  }

  public static void extractResults(Point p, List<NewsMatch> results, Collection<?> dbMatches, int max) {
    int c = 0;
    for (Object o : dbMatches) {
      GeotaggedItem a = (GeotaggedItem) o;
      NewsMatch match = new NewsMatch();
      match.setTitle(a.getTitle());
      Point point = (Point) a.getLocation();
      match.setLongitude(point.getX());
      match.setLatitude(point.getY());
      match.setWhere(a.getWhere());
      match.setUrl(a.getUrl());
      Collection<? extends GeotaggedItem> children = a.getChildren();
      if (children != null) {
        List<NewsMatch> matchChildren = new ArrayList<NewsMatch>();
        extractResults(p, matchChildren, children, max);
        match.setChildren(matchChildren);
      }

      match.setLastPublished(a.getLastPublished());
      try {
        match.setDistance(JTS.orthodromicDistance(p.getCoordinate(), a.getLocation().getCoordinate(),
            DefaultGeographicCRS.WGS84));
      } catch (TransformException e) {
        log.warn("Unable to compute distance.", e);
        match.setDistance(Double.NaN);
      }
      results.add(match);
      if (++c == max) {
        break; // Stop at max
      }
    }
    Collections.sort(results, new DistanceComparator());
  }

}

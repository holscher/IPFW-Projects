package edu.ipfw.cs575.mobilenews.services;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernatespatial.oracle.criterion.OracleSpatialRestrictions;
import org.hibernatespatial.oracle.criterion.SDOParameterMap;

import com.ipfw.cs575.mobilenews.util.Geocoder;
import com.ipfw.cs575.mobilenews.util.GeometryUtils;
import com.vividsolutions.jts.geom.Point;

import edu.ipfw.cs575.mobilenews.data.Article;
import edu.ipfw.cs575.mobilenews.services.data.NewsNear;

@Stateless
public class Articles implements ArticlesLocal {

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
    Criteria criteria = em.unwrap(Session.class).createCriteria(Article.class);
    long earliestPubDate = System.currentTimeMillis() - maxAgeDays * 24 * 60 * 60 * 1000;
    criteria.add(Restrictions.ge("lastPublished", earliestPubDate));
    criteria.add(Restrictions.sqlRestriction("rownum <= ?", maxNeighbors, IntegerType.INSTANCE));
    criteria.add(OracleSpatialRestrictions.SDONN("location", p, sdoParameterMap));
    Stories.extractResults(p, result.getMatches(), criteria.list(), maxNeighbors);
    return result;
  }

  /* (non-Javadoc)
   * @see edu.ipfw.cs575.mobilenews.services.ArticlesLocal#getNear(double, double, double)
   */
  @Override
  public NewsNear getNeighbors(double longitude, double latitude, int maxNeighbors) {
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
    sdoParameterMap.setUnit("METER");
    sdoParameterMap.setSdoNumRes(maxNeighbors);
    Point p = GeometryUtils.createPoint(longitude, latitude);
    Criteria criteria = em.unwrap(Session.class).createCriteria(Article.class);
    criteria.add(OracleSpatialRestrictions.SDONN("location", p, sdoParameterMap));
    Stories.extractResults(p, result.getMatches(), criteria.list(), maxNeighbors);
    return result;
  }

  /* (non-Javadoc)
   * @see edu.ipfw.cs575.mobilenews.services.ArticlesLocal#getNear(double, double, double)
   */
  @Override
  public NewsNear getNear(double longitude, double latitude, double distanceMeters, int max, int maxAgeDays) {
    NewsNear result = new NewsNear();
    result.setDistance(distanceMeters);
    result.setLatitude(latitude);
    result.setLongitude(longitude);
    //result.setWhere(Geocoder.reverseGeocodeToName(longitude, latitude));
    SDOParameterMap sdoParameterMap = new SDOParameterMap();
    sdoParameterMap.setUnit("METER");
    sdoParameterMap.setDistance(distanceMeters);
    Point p = GeometryUtils.createPoint(longitude, latitude);
    /*
     * To use Hibernate spatial we have to drop out of JPA and use Hibernate
     * specific classes..
     */
    Criteria criteria = em.unwrap(Session.class).createCriteria(Article.class);
    criteria.add(OracleSpatialRestrictions.SDOWithinDistance("location", p, sdoParameterMap));

    Stories.extractResults(p, result.getMatches(), criteria.list(), max);
    return result;
  }

}

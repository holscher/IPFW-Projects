package edu.ipfw.cs575.mobilenews.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;

@Entity
@Table(name="story")
@org.hibernate.annotations.Table(indexes={@Index(name="cluster_lastpublished", columnNames = { "centroid" } )}, appliesTo="story")
@NamedQueries({@NamedQuery(name = "PotentialClusters", query = "select c from Cluster c join c.terms t where c.centroid >= :oldestAllowed and t.clusterKey.term in (:topTerms)")})
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Cluster extends NewsEntity implements GeotaggedItem {

  private static final long serialVersionUID = 1L;
  private String title;
 
  @Type(type = "org.hibernatespatial.GeometryUserType")
  private Point location;

  @Column(length = 100, name="place_name")
  private String where;

  private long lastPublished;

  private long centroid;
  
  @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="clusterKey.cluster")
  private Set<ClusterTerm>terms;

  @OneToMany(cascade={CascadeType.DETACH}, fetch=FetchType.LAZY, mappedBy="cluster")
  private Set<Article>articles;

  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }

  public Point getLocation() {
    return location;
  }

  public void setLocation(Point location) {
    this.location = location;
  }

  public long getLastPublished() {
    return lastPublished;
  }
  public void setLastPublished(long lastPublished) {
    this.lastPublished = lastPublished;
  }
  public long getCentroid() {
    return centroid;
  }
  public void setCentroid(long centroid) {
    this.centroid = centroid;
  }
  public Set<ClusterTerm> getTerms() {
    return terms;
  }
  public void setTerms(Set<ClusterTerm> terms) {
    this.terms = terms;
  }
  public String getWhere() {
    return where;
  }

  public void setWhere(String where) {
    this.where = where;
  }
  public Set<Article> getArticles() {
    return articles;
  }
  @Override
  public Collection<? extends GeotaggedItem> getChildren() {
    return getArticles();
  }
  @Override
  public String getUrl() {
    return null;
  }

  public Map<String, Double> getTFV() {
    Set<ClusterTerm> terms = getTerms();
    final HashMap<String, Double>tfv = new HashMap<String, Double>(terms.size());
    for (ClusterTerm term : terms) {
      tfv.put(term.getClusterKey().getTerm(), term.getTfidf());
    }
    return tfv;
  }

}

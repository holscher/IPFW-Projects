package edu.ipfw.cs575.mobilenews.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

/**
 * Entity implementation class for Entity: Statistics
 * 
 */
@Entity
@NamedQueries({@NamedQuery(name = "Statistics", query = "select s from Statistics s"),
  @NamedQuery(name = "UpdateStatistics", query = "update Statistics t set t.articleCount = t.articleCount + 1")})
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Statistics implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SuppressWarnings("unused")
  private long id = 0;
  
  @Version
  @SuppressWarnings("unused")
  private int version;

  /**
   * Total Aricles
   */
  private int articleCount;
  
  /**
   * Constant used in time attenuation for clusters
   */
  private double timeAttenuationConstant;

  /**
   * Cluster cutoff distance, how it is used
   * depends on the clustering algorithm.
   */
  private double cutoffDistance;

  /**
   * If all article distances are
   * greater than this distance from the location
   * do not cluster;
   */
  private double locationCutoffDistance;
  
  /**
   * Max terms, top scoring terms used in TFIDF
   * Should be less than 50;
   */
  private int maxTerms;

  /**
   * Number of top scoring terms in a document used to find a cluster.
   */
  private int maxTopTerms;

  /**
   * Maximum time before a cluster becomes inactive.
   */
  private int maxClusterTimeCentroid;

  public Statistics() {
    super();
  }

  public double getTimeAttenuationConstant() {
    return timeAttenuationConstant;
  }

  public void setTimeAttenuationConstant(double timeAttenuationConstant) {
    this.timeAttenuationConstant = timeAttenuationConstant;
  }

  public double getCutoffDistance() {
    return cutoffDistance;
  }

  public void setCutoffDistance(double cutoffDistance) {
    this.cutoffDistance = cutoffDistance;
  }

  public double getLocationCutoffDistance() {
    return locationCutoffDistance;
  }

  public void setLocationCutoffDistance(double locationCutoffDistance) {
    this.locationCutoffDistance = locationCutoffDistance;
  }

  public int getMaxTerms() {
    return maxTerms;
  }

  public void setMaxTerms(int maxTerms) {
    this.maxTerms = maxTerms;
  }

  public int getMaxClusterTimeCentroid() {
    return maxClusterTimeCentroid;
  }

  public void setMaxClusterTimeCentroid(int maxClusterTimeCentroid) {
    this.maxClusterTimeCentroid = maxClusterTimeCentroid;
  }

  public int getArticleCount() {
    return articleCount;
  }

  public void setArticleCount(int articleCount) {
    this.articleCount = articleCount;
  }

  public int getMaxTopTerms() {
    return maxTopTerms;
  }

  public void setMaxTopTerms(int maxTopTerms) {
    this.maxTopTerms = maxTopTerms;
  }
}

package edu.ipfw.cs575.mobilenews.data;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

/**
 * Entity implementation class for Entity: ClusterTerm
 * 
 */
@Entity
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ClusterTerm implements Serializable {

  private static final long serialVersionUID = 1L;

  @Embeddable
  public static class ClusterTermPK implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(nullable = false, length = 36)
    private String term;

    @ManyToOne(cascade = { CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id")
    private Cluster cluster;

    public ClusterTermPK() {
    }

    public ClusterTermPK(Cluster cluster, String term) {
      this.cluster = cluster;
      this.term = term;
    }

    public String getTerm() {
      return term;
    }

    public void setTerm(String term) {
      this.term = term;
    }

    public Cluster getCluster() {
      return cluster;
    }

    public void setCluster(Cluster cluster) {
      this.cluster = cluster;
    }

    @Override
    public int hashCode() {
      return term.hashCode() ^ (int) cluster.getId();
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof ClusterTermPK && term.equals(((ClusterTermPK) obj).term)
          && cluster.getId() == ((ClusterTermPK) obj).cluster.getId();
    }
  }

  @Id
  @Embedded
  private ClusterTermPK clusterKey;

  @Version
  @SuppressWarnings("unused")
  private int version;

  private int termCount;
  private double tfidf;

  public ClusterTerm() {
    super();
  }

  public int getTermCount() {
    return termCount;
  }

  public void setTermCount(int termCount) {
    this.termCount = termCount;
  }

  public double getTfidf() {
    return tfidf;
  }

  public void setTfidf(double tfidf) {
    this.tfidf = tfidf;
  }

  public ClusterTermPK getClusterKey() {
    return clusterKey;
  }

  public void setClusterKey(ClusterTermPK clusterKey) {
    this.clusterKey = clusterKey;
  }

}

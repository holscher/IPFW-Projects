package edu.ipfw.cs575.mobilenews.data;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;

/**
 * Entity implementation class for Entity: Article
 * 
 */
@Entity
@XmlRootElement
@Table(name="content", uniqueConstraints = @UniqueConstraint(columnNames = "url"))
@org.hibernate.annotations.Table(indexes={@Index(name="article_lastpublished", columnNames = { "lastPublished" } )}, appliesTo="content")
@NamedQueries({@NamedQuery(name = "ArticleByURL", query = "select a from Article a where url = :url"),
  @NamedQuery(name = "ArticleCount", query = "select count(a) from Article a")})
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Article extends WebResource implements GeotaggedItem {

  private static final long serialVersionUID = 1L;

  @Transient
  private String description;

  @Transient
  private String descriptionType;

  @Transient
  private String content;

  @Transient
  private String contentType;

  @ManyToOne(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY, optional=false)
  @JoinColumn(name = "source_id")
  private Feed source;

  @ManyToOne(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY, optional=true)
  @JoinColumn(name = "cluster_id")
  private Cluster cluster;

  @Column(length = 100, name="place_name")
  private String where;

  @Type(type = "org.hibernatespatial.GeometryUserType")
  private Point location;

  public Article() {
    super();
  }

  public Point getLocation() {
    return location;
  }

  public void setLocation(Point location) {
    this.location = location;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescriptionType() {
    return descriptionType;
  }

  public void setDescriptionType(String descriptionType) {
    this.descriptionType = descriptionType;
  }

  public Feed getSource() {
    return source;
  }

  public void setSource(Feed source) {
    this.source = source;
  }

  public String getWhere() {
    return where;
  }

  public void setWhere(String where) {
    this.where = where;
  }

  public Cluster getCluster() {
    return cluster;
  }

  public void setCluster(Cluster cluster) {
    this.cluster = cluster;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  @Override
  public Collection<? extends GeotaggedItem> getChildren() {
    return null;
  }

}

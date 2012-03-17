package edu.ipfw.cs575.mobilenews.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class WebResource extends NewsEntity {
  private static final long serialVersionUID = 1L;
  @Column(length = 2048, nullable = false)
  private String url;
  @Column(length = 500)
  private String title;
  @Column(length = 1024)
  private String eTag;
  @Column(length = 50)
  private String lastModified;
  private long lastScanned;
  private long lastPublished;
  private long created = System.currentTimeMillis();
  private long updated = System.currentTimeMillis();

  @PrePersist
  void setCreated() {
    created = System.currentTimeMillis();
    updated = System.currentTimeMillis();

  }
  @PreUpdate
  void setUpdated() {
    updated = System.currentTimeMillis();

  }
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public long getLastScanned() {
    return lastScanned;
  }

  public void setLastScanned(long lastScanned) {
    this.lastScanned = lastScanned;
  }

  public String geteTag() {
    return eTag;
  }

  public void seteTag(String eTag) {
    this.eTag = eTag;
  }

  public String getLastModfied() {
    return lastModified;
  }

  public void setLastModfied(String lastModfied) {
    this.lastModified = lastModfied;
  }

  public long getLastPublished() {
    return lastPublished;
  }

  public void setLastPublished(long lastPublished) {
    this.lastPublished = lastPublished;
  }

  public long getCreated() {
    return created;
  }

  public long getUpdated() {
    return updated;
  }
  @Override
  public String toString() {
    return getTitle();
  }
}

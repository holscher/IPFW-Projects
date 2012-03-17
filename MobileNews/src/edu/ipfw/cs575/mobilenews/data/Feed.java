package edu.ipfw.cs575.mobilenews.data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames="url"))
public class Feed extends WebResource {

  private static final long serialVersionUID = 1L;

  private boolean geoRSS;
  
  private int errorCount;

  private boolean enabled;

  public boolean isGeoRSS() {
    return geoRSS;
  }

  public void setGeoRSS(boolean geoRSS) {
    this.geoRSS = geoRSS;
  }

  public void setErrorCount(int errorCount) {
    this.errorCount = errorCount;
  }

  public int getErrorCount() {
    return errorCount;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}

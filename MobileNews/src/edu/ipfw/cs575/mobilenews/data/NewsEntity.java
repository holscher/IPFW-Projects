package edu.ipfw.cs575.mobilenews.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
@SequenceGenerator(
        name="MOBLIE_NEWS_GENERATOR",
        sequenceName="MOBLIE_NEWS_SEQ",
        initialValue=1,
        allocationSize=5
    )
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class NewsEntity implements Serializable {
  
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MOBLIE_NEWS_GENERATOR")
  private long id;
  
  @Version
  @SuppressWarnings("unused")
  private int version;

  public long getId() {
    return id;
  }
}

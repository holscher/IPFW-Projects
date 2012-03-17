package edu.ipfw.cs575.mobilenews.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

/**
 * Entity implementation class for Entity: Term
 * 
 */
@Entity
@NamedQueries({@NamedQuery(name = "GetTerm", query = "select t from Term t where t.term = :term"),
  @NamedQuery(name = "UpdateTerm", query = "update Term t set t.articleCount = t.articleCount + 1 where t.term = :term")})
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Term implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(nullable=false, length=36)
  @Id
  private String term;
 
  @Version
  @SuppressWarnings("unused")
  private int version;

  private int articleCount;

  public Term() {
  }
  public String getTerm() {
    return term;
  }
  public void setTerm(String term) {
    this.term = term;
  }
  public int getArticleCount() {
    return articleCount;
  }
  public void incrementCount() {
    articleCount++;
  }
}

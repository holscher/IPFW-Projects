package edu.ipfw.cs575.mobilenews.collect;

import javax.ejb.Schedule;
import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import edu.ipfw.cs575.mobilenews.data.Statistics;

/**
 * This class runs on a regular basis and
 * updates statistics.
 * 
 */
//@Singleton
public class StatisticsUpdater {

  @PersistenceContext(unitName = "MobileNews")
  EntityManager em;

  @Schedule(minute = "*/5", hour = "*", second = "0", persistent = false)
  public void updateStats() throws JMSException {
    TypedQuery<Statistics> q = em.createNamedQuery("Statistics", Statistics.class);
    Statistics stats = q.getSingleResult();
    Number articleCount = (Number)em.createNamedQuery("ArticleCount").getSingleResult();
    stats.setArticleCount(articleCount.intValue());
//    /* There might be a more efficient way to do this */
//    Query termUpdate = em.createQuery("update Term t set t.termCount = (select sum(termCount) from ClusterTerm ct where t.term = ct.term");
//    termUpdate.executeUpdate();
//    List<ClusterTerm> list = em.createQuery("select ct from ClusterTerm ct left join Term t where t.term is null", ClusterTerm.class).getResultList();
//    /* Terms that aren't in the document list yet */
//    for (ClusterTerm ct : list) {
//      Term t = new Term();
//      t.setTerm(ct.getTerm());
//      t.setTermCount(ct.getTermCount());
//      em.persist(t);
//    }
  }

}

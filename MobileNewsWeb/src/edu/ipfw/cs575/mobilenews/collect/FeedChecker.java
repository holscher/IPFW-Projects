package edu.ipfw.cs575.mobilenews.collect;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jmx.StatisticsService;

import edu.ipfw.cs575.mobilenews.data.Feed;
import edu.ipfw.cs575.mobilenews.data.WebResource;

@Singleton
public class FeedChecker {
  private static final Logger log = Logger.getLogger(FeedChecker.class);

  private static final long MINUMUM_TIME_BETWEEN_SCANS = 20 * 60 * 1000; /* minutes */

  @PersistenceContext(unitName = "MobileNews")
  EntityManager em;

  @Resource(mappedName = "/queue/MobileNews/feeds")
  private Queue queue;

  @Resource(mappedName = "java:/JmsXA")
  private ConnectionFactory connectionFactory;

  private volatile boolean registered = false;

  @SuppressWarnings("unused")
  @PostConstruct
  private void registerHibernateMBean() {
    if (!registered) {
      registered = true;
      // MBean service registration for all SessionFactory's
      Hashtable<String, String> tb = new Hashtable<String, String>();
      tb.put("type", "statistics");
      tb.put("sessionFactory", "all");
      try {
        ObjectName on = new ObjectName("hibernate", tb);
        // MBean object name

        StatisticsService stats = new StatisticsService(); // MBean
                                                           // implementation
        SessionFactory sessionFactory = em.unwrap(Session.class).getSessionFactory();
        stats.setSessionFactory(sessionFactory);
        ArrayList<MBeanServer> list = MBeanServerFactory.findMBeanServer(null);
        // take the first one
        list.get(0).registerMBean(stats, on); // Register the MBean
      } catch (Exception e) {
        log.warn("Unable to register hibnerate statistics ", e);
      }
    }
  }

  @Schedule(minute = "*/1", hour = "*", second = "0", persistent = false)
  public void checkFeeds() throws JMSException {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Feed> q = cb.createQuery(Feed.class);
    Root<Feed> feedRoot = q.from(Feed.class);
    Predicate leScanned = cb.le(feedRoot.<Number> get("lastScanned"), System.currentTimeMillis()
        - MINUMUM_TIME_BETWEEN_SCANS);
    Predicate enabled = cb.equal(feedRoot.<Boolean> get("enabled"), true);
    q.where(cb.and(leScanned, enabled));
    // System.currentTimeMillis()));
    Connection connection = connectionFactory.createConnection();
    try {
      javax.jms.Session session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
      try {
        MessageProducer messageProducer = session.createProducer(queue);
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        try {
          for (WebResource feed : em.createQuery(q).getResultList()) {
            em.detach(feed);
            messageProducer.send(session.createObjectMessage(feed));
          }
        } finally {
          messageProducer.close();
        }
      } finally {
        session.close();
      }
    } finally {
      connection.close();
    }

  }

}

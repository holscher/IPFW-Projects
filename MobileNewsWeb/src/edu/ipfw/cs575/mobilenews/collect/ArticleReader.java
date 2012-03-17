package edu.ipfw.cs575.mobilenews.collect;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.ipfw.cs575.mobilenews.util.Geocoder;
import com.vividsolutions.jts.geom.Point;

import edu.ipfw.cs575.mobilenews.data.Article;

@MessageDriven(mappedName = "queue/MobileNews/articles", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/MobileNews/articles"),
    @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "10") })
public class ArticleReader implements MessageListener {
  private static final Logger log = Logger.getLogger(ArticleReader.class);

  @PersistenceContext(unitName = "MobileNews")
  EntityManager em;


  @Resource(mappedName = "/queue/MobileNews/cluster")
  private Queue queue;

  @Resource(mappedName = "java:/JmsXA")
  private ConnectionFactory connectionFactory;

  /** Need HTTP client to fetch artcles and check headers */
//  private final HttpClient httpClient = new DefaultHttpClient();


  @Override
  public void onMessage(Message feedMessage) {
    try {
      Article newEntry = (Article) ((ObjectMessage) feedMessage).getObject();
      Query q = em.createNamedQuery("ArticleByURL");
      q.setParameter("url", newEntry.getUrl());
      List<?> articleList = q.getResultList();
      if (articleList.size() > 0) {
        Article a = (Article) articleList.get(0);
        if (newEntry.getLastPublished() > a.getLastPublished()) {
          /* Article was updated, reprocess */
          a.setTitle(newEntry.getTitle());
          a.setLastPublished(newEntry.getLastPublished() == 0 ? System.currentTimeMillis() : newEntry.getLastPublished());
          if (newEntry.getLocation() != null) {
            a.setLocation(newEntry.getLocation());
          }
          a.setUrl(newEntry.getUrl());
          if (newEntry.getDescription() != null) {
            a.setDescription(newEntry.getDescription());
            a.setDescriptionType(newEntry.getDescriptionType());
          }
          // Articles are not reclustered for efficiency
          //clusterArticle(a);
          log.info("Updated article info " + a.getTitle() + " at " + a.getLocation().toText());
        } 
      } else {
        Point location = newEntry.getLocation();
        if (location == null) {
          log.debug("No geolocation for " + newEntry.getTitle() + " dropping.");
        } else {
          retrieveContent(newEntry);
          log.info("Adding article " + newEntry.getTitle() + " at " + newEntry.getLocation().toText());
          if (newEntry.getLastPublished() == 0) {
            newEntry.setLastPublished(System.currentTimeMillis());
          }
          newEntry.setWhere(Geocoder.reverseGeocodeToName(location.getCoordinate().x, location.getCoordinate().y));
          em.persist(newEntry);
          clusterArticle(newEntry);
        }
      }
      em.flush();
    } catch (Exception e) {
      log.error("Error handling JMS messsage for article.", e);
    }
  }

  /**
   * Extracts the content from the article
   * @param artlcle
   * @throws IOException
   */
  private void retrieveContent(Article artlcle) throws IOException {
//    HttpUriRequest httpRequest = new HttpGet(artlcle.getUrl());
//    HttpResponse response = httpClient.execute(httpRequest);
//    artlcle.setContentType(response.getEntity().getContentType().getValue());
//    artlcle.setContent(EntityUtils.toString(response.getEntity()));
//    response.getEntity().consumeContent();
  }

  /**
   * Drop Article onto cluster queue
   */
  private void clusterArticle(Article a) throws JMSException {
    // System.currentTimeMillis()));
    Connection connection = connectionFactory.createConnection();
    try {
      Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
      try {
        MessageProducer messageProducer = session.createProducer(queue);
        try {
          messageProducer.send(session.createObjectMessage(a));
        } finally {
          messageProducer.close();
        }
       // session.commit();
      } finally {
        session.close();
      }
    } finally {
      connection.close();
    }

  }


}

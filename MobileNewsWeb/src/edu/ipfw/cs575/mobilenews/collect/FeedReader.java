package edu.ipfw.cs575.mobilenews.collect;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Source;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.ipfw.cs575.mobilenews.util.GeometryUtils;
import com.sun.syndication.feed.module.georss.GeoRSSModule;
import com.sun.syndication.feed.module.georss.GeoRSSUtils;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.vividsolutions.jts.geom.Point;

import edu.ipfw.cs575.mobilenews.data.Article;
import edu.ipfw.cs575.mobilenews.data.Feed;

@MessageDriven(mappedName = "queue/MobileNews/feeds", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/MobileNews/feeds"),
    @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "2") })
public class FeedReader implements MessageListener {
  private static final Logger log = Logger.getLogger(FeedReader.class);

  /** GeoNames RSS encoder */
  private static String GEONAMES_GEORSS = "http://ws.geonames.org/rssToGeoRSS?";
  
  /** Random adjustment to make place distances more interesting */
  private static double RANDOM_DEGREES = 0.05; /* About 5.5k at equator */
    
  @PersistenceContext(unitName = "MobileNews")
  EntityManager em;

  @Resource(mappedName = "/queue/MobileNews/articles")
  private Queue queue;

  @Resource(mappedName = "java:/JmsXA")
  private ConnectionFactory connectionFactory;

  /** Need HTTP client to fetch feeds and check headers */
  private final HttpClient httpClient = new DefaultHttpClient();

  /** Need a random number generator */
  private final Random random = new Random();
  
  @Override
  public void onMessage(Message feedMessage) {
    try {

      Feed feed = (Feed) ((ObjectMessage) feedMessage).getObject();
      try {

        feed.setLastScanned(System.currentTimeMillis());
        /* Check if the server says the feed changed */
        SyndFeed wireFeed = checkFeed(feed);
        if (wireFeed != null) {
          if (wireFeed.getLanguage() == null || wireFeed.getLanguage().startsWith("en")) {
            if (wireFeed.getTitle() != null && !wireFeed.getTitle().equals(feed.getTitle())) {
              feed.setTitle(wireFeed.getTitle());
            }
            if (wireFeed.getPublishedDate() != null) {
              feed.setLastPublished(wireFeed.getPublishedDate().getTime());
            }
            Iterator<?> items = wireFeed.getEntries().iterator();
            Connection connection = connectionFactory.createConnection();
            try {
              Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
              try {
                MessageProducer messageProducer = session.createProducer(queue);
                messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                try {
                  while (items.hasNext()) {
                    SyndEntry entry = (SyndEntry) items.next();
                    GeoRSSModule geoRSSModule = GeoRSSUtils.getGeoRSS(entry);
                    if (entry.getLink() == null) {
                      log.error("No link for " + entry.getTitle());
                    } else if (entry.getTitle() == null) {
                      log.error("No title for " + entry.getTitle());
                    } else {
                      Article a = new Article();
                      a.setSource(feed);
                      a.setTitle(cleanTitle(entry.getTitle()));
                      a.setLastPublished(entry.getPublishedDate() == null ? (wireFeed.getPublishedDate() == null ? 0 : wireFeed.getPublishedDate().getTime()) : entry.getPublishedDate().getTime());
                      if (geoRSSModule != null && geoRSSModule.getPosition() != null) {
                        a.setLocation(getLocation(feed.isGeoRSS(), geoRSSModule));
                      }
                      a.setUrl(entry.getLink());
                      if (entry.getDescription() != null) {
                        a.setDescription(entry.getDescription().getValue());
                        a.setDescriptionType(entry.getDescription().getType());
                      }
                      messageProducer.send(session.createObjectMessage(a));
                    }
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
          } else {
            log.info("Feed " + feed.getTitle() + " is not in english");
          }
        }
        em.merge(feed);
      } catch (Exception e) {
        log.error("Error fetching feed " + feed.getTitle(), e);
      }
    } catch (Exception e) {
      log.error("Error handling JMS messsage for feed.", e);
    }
  }

  private String cleanTitle(String title) {
    return new Renderer(new Source(title)).toString();
  }

  private Point getLocation(boolean geoEnabled, GeoRSSModule geoRSSModule) {
    double longitude = geoRSSModule.getPosition().getLongitude();
    double latitude = geoRSSModule.getPosition().getLatitude();
    if (!geoEnabled) {
      /* Geonames only does an OK job of Geolocating, this is a hack to vary the story locations
       * a little bit so the results are more interesting.
       */
      longitude += RANDOM_DEGREES * 2.0 * (random.nextDouble() - 0.5);
      latitude += RANDOM_DEGREES * 2.0 * (random.nextDouble() - 0.5);
    }
    return GeometryUtils.createPoint(longitude, latitude);
  }

  /**
   * Returns a SyndFeed if the feed hasn't changed and it isn't responding with
   * an error
   * 
   * @param feed
   *          the feed to check
   * @return
   */
  protected SyndFeed checkFeed(Feed feed) {
    String etag = feed.geteTag();
    String lastModified = feed.getLastModfied();
    HttpUriRequest httpRequest;
    if (feed.isGeoRSS()) {
      /*
       * Feed does not have to be wrapped by GeoNames so we'll get it all at
       * once if it hasn't changed
       */
      httpRequest = new HttpGet(feed.getUrl());
    } else {
      /*
       * Feed has to be wrapped by Geo names so we'll check the content first
       * with an HTTP head then pass it through geonames later
       */
      httpRequest = new HttpGet(feed.getUrl());
    }
    if (etag != null) {
      httpRequest.setHeader("If-None-Match", etag);
    }
    if (lastModified != null) {
      /* Send back exact header we were sent */
      httpRequest.setHeader("If-Modified-Since", lastModified);
//    } else if (feed.getLastScanned() != 0){
//      httpRequest.setHeader("If-Modified-Since", DateUtils.formatDate(new Date(feed.getLastScanned())));
    }
    try {
      HttpResponse response = httpClient.execute(httpRequest);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
        log.info("Feed " + feed.getTitle() + " not modified.");
        return null;
      } else if (statusCode >= 200 && statusCode <= 300) {
        Header[] headers = response.getAllHeaders();
        feed.seteTag(null);
        feed.setLastModfied(null);
        for (Header header : headers) {
          if (header.getName().equalsIgnoreCase("etag")) {
            log.info("Feed " + feed.getTitle() + " etag " + header.getValue());
            feed.seteTag(header.getValue());
          } else if (header.getName().equalsIgnoreCase("last-modified")) {
            feed.setLastModfied(header.getValue());
            log.info("Feed " + feed.getTitle() + " last modified " + header.getValue());
          }
        }
        HttpEntity entity = response.getEntity();
        try {
          if (feed.isGeoRSS()) {
            log.info("Reading GeoRSS Feed " + feed.getTitle());
            return new SyndFeedInput().build(new XmlReader(entity.getContent()));
          } else {
            log.info("Making GeoRSS Feed " + feed.getTitle());
            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
            qparams.add(new BasicNameValuePair("feedUrl", feed.getUrl()));
            String queryStr = URLEncodedUtils.format(qparams, "UTF-8");
            URL url = new URL(GEONAMES_GEORSS + queryStr);
            try {
              return new SyndFeedInput().build(new XmlReader(url));
            } catch (Exception e) {
              log.error("Error checking feed using geonames " + feed.getTitle() + " url:" + url);
              return null;
            }
          }
        } finally {
          if (entity != null) {
            try {
              entity.getContent().close();
            } catch (Exception e) {
              // OK, to close
            }
          }
        }
      } else {
        log.warn("Error reading feed " + feed.getTitle() + " http status " + statusCode);
        feed.setErrorCount(feed.getErrorCount() + 1);
        return null;
      }
    } catch (Exception e) {
      log.error("Error checking feed " + feed.getTitle() + " url:" + feed.getUrl());
      return null;
    }
  }

}

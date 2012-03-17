package edu.ipfw.cs575.mobilenews.cluster;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernatespatial.oracle.criterion.OracleSpatialRestrictions;
import org.hibernatespatial.oracle.criterion.SDOParameterMap;
import org.tartarus.snowball.ext.EnglishStemmer;

import edu.ipfw.cs575.mobilenews.data.Article;
import edu.ipfw.cs575.mobilenews.data.Cluster;
import edu.ipfw.cs575.mobilenews.data.ClusterTerm;
import edu.ipfw.cs575.mobilenews.data.ClusterTerm.ClusterTermPK;
import edu.ipfw.cs575.mobilenews.data.Statistics;

//@MessageDriven(mappedName = "queue/MobileNews/cluster", activationConfig = {
//    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
//    @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/MobileNews/cluster"),
//    @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "4")})
public class JaccardArticleClusterer implements MessageListener {
  private static final Logger log = Logger.getLogger(JaccardArticleClusterer.class);

  final private static Pattern MOSTLY_ALPHA = Pattern.compile("^[a-z']+$", Pattern.CASE_INSENSITIVE);

  @PersistenceContext(unitName = "MobileNews")
  private EntityManager em;
  private Tokenizer tokenizer;
  private TokenStream tokenStream;

  @Override
  public void onMessage(Message feedMessage) {
    try {
      Article newArticle = (Article) ((ObjectMessage) feedMessage).getObject();
      Article mergedArticle = em.merge(newArticle);
      Set<String> terms = new LinkedHashSet<String>();
      extractTerms(terms, mergedArticle.getTitle());
      extractTerms(terms, newArticle.getDescription());
      extractTerms(terms, newArticle.getContent());
      TypedQuery<Statistics> q = em.createNamedQuery("Statistics", Statistics.class);
      q.setLockMode(LockModeType.NONE);
      Statistics stats = q.getSingleResult();
      /* Find possible matching clusters, they have to be new enough
       * and match at least one top term(s) from the article.
       */
      /* For this algorithm we define top terms as those that come first */
      List<String>topTerms = new ArrayList<String>(terms.size());
      int i = 0;
      int maxTopTerms = stats.getMaxTopTerms();
      for (String term : terms) {
        topTerms.add(term);
        if (++i == maxTopTerms)
            break;
      }
      
      //result.setWhere(Geocoder.reverseGeocodeToName(longitude, latitude));
      SDOParameterMap sdoParameterMap = new SDOParameterMap();
      sdoParameterMap.setUnit("METER");
      sdoParameterMap.setDistance(stats.getLocationCutoffDistance());
      /*
       * To use Hibernate spatial we have to drop out of JPA and use Hibernate
       * specific classes..
       */
      Criteria criteria = em.unwrap(Session.class).createCriteria(Cluster.class);
      criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
      criteria.add(OracleSpatialRestrictions.SDOWithinDistance("location", mergedArticle.getLocation(), sdoParameterMap));
      criteria.add(Restrictions.ge("centroid", System.currentTimeMillis() - stats.getMaxClusterTimeCentroid()));
      criteria.createCriteria("terms").add(Restrictions.in("clusterKey.term", topTerms));
      
      Cluster closestCluster =  null;
      double similiarity = Integer.MIN_VALUE;
      for (Object o : criteria.list()) {
        Cluster cluster = (Cluster)o;
        double thisSimiliarity = computeJaccard(cluster, mergedArticle.getLastPublished(), stats.getMaxClusterTimeCentroid(), terms);
        System.out.println("Similiarity of " + mergedArticle + " to " + cluster.getTitle() + " is " + thisSimiliarity);
        if (thisSimiliarity > similiarity) {
          similiarity = thisSimiliarity;
          closestCluster = cluster;
        }
      }
      if (closestCluster != null && similiarity > stats.getCutoffDistance()) {
        log.info("Adding " + mergedArticle.getTitle() + " to " + closestCluster.getTitle());
        Set<String>existingClusterTerms = new HashSet<String>(closestCluster.getTerms().size());
        Set<ClusterTerm> clusterTerms = closestCluster.getTerms();
        for (ClusterTerm clusterTerm : clusterTerms) {
          String term = clusterTerm.getClusterKey().getTerm();
          existingClusterTerms.add(term);
        }
        for (String articleTerm : terms) {
          if (!existingClusterTerms.contains(articleTerm)) {
            addClusterTerm(closestCluster, clusterTerms, articleTerm);
          }
        }
        /* Not going to recenter the cluster */
        /* This is not an mean, but a moving average */
        closestCluster.setCentroid(closestCluster.getCentroid() / 2 + mergedArticle.getLastPublished() / 2);
        if (mergedArticle.getLastPublished() > closestCluster.getLastPublished()) {
          closestCluster.setLastPublished(mergedArticle.getLastPublished());
        }
        mergedArticle.setCluster(closestCluster);
      } else {
        log.info("Creating cluster for " + mergedArticle.getTitle());
        /* New cluster */
        Cluster cluster = new Cluster();
        cluster.setLastPublished(mergedArticle.getLastPublished());
        cluster.setCentroid(mergedArticle.getLastPublished());
        /* Title of first article wins for now */
        cluster.setTitle(mergedArticle.getTitle());
        /* Location of first article wins for now */
        cluster.setLocation(mergedArticle.getLocation());
        cluster.setWhere(mergedArticle.getWhere());
        Set<ClusterTerm>newTerms = new HashSet<ClusterTerm>(terms.size());
        for (String articleTerm : terms) {
          addClusterTerm(cluster, newTerms, articleTerm);
        }
        cluster.setTerms(newTerms);
        mergedArticle.setCluster(cluster);
        em.persist(cluster);
      }
    } catch (JMSException e) {
      log.error("Error handling JMS messsage for article.", e);
    } catch (IOException e) {
      throw new IllegalStateException("Error processing terms", e);
    }
  }


  private void addClusterTerm(Cluster cluster, Set<ClusterTerm> newTerms, String articleTerm) {
    ClusterTerm term = new ClusterTerm();
    term.setClusterKey(new ClusterTermPK(cluster, articleTerm));
    newTerms.add(term);
  }


  private double computeJaccard(Cluster cluster, long lastPublished, int maxAge, Set<String> articleTerms) {
    HashSet<String>intersection = new HashSet<String>(articleTerms);
    HashSet<String>union = new HashSet<String>(articleTerms);
    for (ClusterTerm clusterTerm : cluster.getTerms()) {
      String term = clusterTerm.getClusterKey().getTerm();
      intersection.remove(term);
      union.add(term);
    }
    double jSimiliary = intersection.size() / (double)union.size();
    /* Add time attenuation */
    long timeDiff = lastPublished - cluster.getCentroid();
    timeDiff *= timeDiff;
    double timeAtten = Math.exp(-timeDiff / (3.0 * maxAge * maxAge) );
    return jSimiliary * timeAtten;
  }


  private int extractTerms(Set<String> terms, String text) throws IOException {
    int total = 0;
    TokenStream stream = tokenizer(text);
    TermAttribute termAttribute = stream.getAttribute(TermAttribute.class);
    TypeAttribute typeAttribute = stream.getAttribute(TypeAttribute.class);
    while (stream.incrementToken()) {
      if (typeAttribute.type().equals(StandardTokenizer.TOKEN_TYPES[StandardTokenizer.ALPHANUM])) {
        String term = termAttribute.term();
        if (MOSTLY_ALPHA.matcher(term).matches()) {
          total++;
          terms.add(term);
        }
      }
    }
    return total;
  }


  private TokenStream tokenizer(String content) throws IOException {
    return tokenizer(new StringReader(content));
  }
  private TokenStream tokenizer(Reader reader) throws IOException {
    if (tokenizer == null) {
      tokenizer = new StandardTokenizer(Version.LUCENE_30, createHTMLExtractor(reader));
      TokenStream result = new StandardFilter(tokenizer);
      result = new ASCIIFoldingFilter(result);
      result = new LowerCaseFilter(result);
      result = new StopFilter(false, result, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
      result = new SnowballFilter(result, new EnglishStemmer());
      tokenStream = result;
    } else {
      tokenizer.reset(createHTMLExtractor(reader));
    }
    return tokenStream;

  }


  private StringReader createHTMLExtractor(Reader reader) throws IOException {
    return new StringReader(new TextExtractor(new Source(reader)).toString());
  }
}

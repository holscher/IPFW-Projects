package edu.ipfw.cs575.mobilenews.cluster;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import edu.ipfw.cs575.mobilenews.data.Term;

// Limit to 1 session for now, need better strategy to avoid contention when clustering
@MessageDriven(mappedName = "queue/MobileNews/cluster", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/MobileNews/cluster"),
    @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1")})
public class TFIDFArticleClusterer implements MessageListener {
  private static final Logger log = Logger.getLogger(TFIDFArticleClusterer.class);

  final private static Pattern MOSTLY_ALPHA = Pattern.compile("^[a-z']+$", Pattern.CASE_INSENSITIVE);

  @PersistenceContext(unitName = "MobileNews")
  private EntityManager em;
  private Tokenizer tokenizer;
  private TokenStream tokenStream;

  @Override
  public void onMessage(Message feedMessage) {
    try {
      Article newArticle = (Article) ((ObjectMessage) feedMessage).getObject();
      /* Don't use merge - may have updated in flight */
      Article mergedArticle = em.find(Article.class, newArticle.getId());
      Map<String, Integer> terms = new HashMap<String, Integer>();
      int total = extractTerms(terms, newArticle.getTitle());
      total += extractTerms(terms, newArticle.getDescription());
      total += extractTerms(terms, newArticle.getContent());
      Statistics stats = em.find(Statistics.class, 0L);
      int dc = stats.getArticleCount() + 1;
      final Map<String, Double> articleTFV = new HashMap<String, Double>();
      List<String>sortedTerms = new ArrayList<String>(terms.keySet());
      Collections.sort(sortedTerms);
      for (String term : sortedTerms) {
        int articleCount = getTerm(term);
        double tfidf = (terms.get(term) / (double)total) * Math.log(dc / (double)articleCount);
        articleTFV.put(term, tfidf);
      }
      stats.setArticleCount(dc);
      /* Find possible matching clusters, they have to be new enough
       * and match at least one top term from the article.
       */
      Collections.sort(sortedTerms, new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
          double tfidf1 = articleTFV.get(o1);
          double tfidf2 = articleTFV.get(o2);
          if (tfidf1 > tfidf2) {
            return -1;
          } else if (tfidf2 > tfidf1) {
            return 1;
          } else 
            return 0;
        }
      });
      int termCount = sortedTerms.size();
      List<String> topTerms = sortedTerms.subList(0, termCount > stats.getMaxTopTerms() ? stats.getMaxTopTerms() : termCount);
      System.out.println("Top terms for " + mergedArticle.getTitle() + ": " + topTerms);
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

      double cosine = Integer.MIN_VALUE;
      Cluster closestCluster =  null;
      for (Object o : criteria.list()) {
        Cluster cluster = (Cluster)o;
        double thisCosine = computeCosine(cluster, articleTFV, mergedArticle.getLastPublished(), stats.getMaxClusterTimeCentroid());
        System.out.println("Similiarity of " + mergedArticle.getTitle() + " to " + cluster.getTitle() + " is " + thisCosine);
        if (thisCosine > cosine) {
          cosine = thisCosine;
          closestCluster = cluster;
        }
      }
      if (closestCluster != null && cosine > stats.getCutoffDistance()) {
        log.info("Adding " + mergedArticle.getTitle() + " to " + closestCluster.getTitle());
        Set<String>existingClusterTerms = new HashSet<String>(closestCluster.getTerms().size());
        Set<ClusterTerm> clusterTerms = closestCluster.getTerms();
        int totalClusterTerms = total;
        for (ClusterTerm clusterTerm : clusterTerms) {
          String term = clusterTerm.getClusterKey().getTerm();
          totalClusterTerms += clusterTerm.getTermCount();
          existingClusterTerms.add(term);
        }
        for (ClusterTerm clusterTerm : clusterTerms) {
          String term = clusterTerm.getClusterKey().getTerm();
          Integer newCount = terms.get(term);
          if (newCount != null) {
            int articleCount = getTerm(term);
            int clusterTermCount = clusterTerm.getTermCount() + newCount;
            clusterTerm.setTermCount(clusterTermCount);
            double tfidf = (clusterTermCount / (double)totalClusterTerms) * Math.log(dc / (double)articleCount);
            clusterTerm.setTfidf(tfidf);            
          }
        }
        for (Entry<String, Double> articleTerm : articleTFV.entrySet()) {
          String term = articleTerm.getKey();
          if (!existingClusterTerms.contains(term)) {
            int articleCount = getTerm(term);
            int clusterTermCount = terms.get(term);
            double tfidf = (clusterTermCount / (double)totalClusterTerms) * Math.log(dc / (double)articleCount);
            addClusterTerm(closestCluster, clusterTerms, term, tfidf, clusterTermCount);
          }
        }
        /* Not going to recenter the cluster location */
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
        for (Entry<String, Double> articleTerm : articleTFV.entrySet()) {
          String term = articleTerm.getKey();
          addClusterTerm(cluster, newTerms, term, articleTerm.getValue(), terms.get(term));
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

  private void addClusterTerm(Cluster cluster, Set<ClusterTerm> newTerms, String articleTerm, double tfidf, int termCount) {
    ClusterTerm term = new ClusterTerm();
    term.setClusterKey(new ClusterTermPK(cluster, articleTerm));
    term.setTfidf(tfidf);
    term.setTermCount(termCount);
    newTerms.add(term);
  }


  private double computeCosine(Cluster cluster, Map<String, Double> articleTFV, long lastPublished, double maxAge) {
    Map<String, Double> clusterTFV = cluster.getTFV();
    double cTFVlen = getLength(clusterTFV);
    double aTFVlen = getLength(articleTFV);
    // We only have to iterage through one Map for terms
    // because if the term isn't in the other map the result is 0
    double dot = 0;
    for (Entry<String, Double> articleTerm : articleTFV.entrySet()) {
      Double clusterTFIDF = clusterTFV.get(articleTerm.getKey());
      if (clusterTFIDF != null) {
        dot += articleTerm.getValue() * clusterTFIDF;
      }
    }
    double cosine =  dot / (cTFVlen * aTFVlen);
    /* Add time attenuation */
    long timeDiff = lastPublished - cluster.getCentroid();
    timeDiff *= timeDiff;
    double timeAtten = Math.exp(-timeDiff / (3.0 * maxAge * maxAge) );
    return cosine * timeAtten;
  }

  private double getLength(Map<String, Double> articleTFV) {
    double sum = 0;
    for(Double d : articleTFV.values()) {
      sum += d * d;
    }
    return Math.sqrt(sum);
  }


  private int getTerm(String key) {
    Term term = em.find(Term.class, key);
    if (term == null) {
      term = new Term();
      term.setTerm(key);
      /* To avoid dividing by 0 include the current article in corpus */
      term.incrementCount();
      em.persist(term);
    } else {
      term.incrementCount();
    }
    return term.getArticleCount();
  }


  private int extractTerms(Map<String, Integer> terms, String text) throws IOException {
    int total = 0;
    if (text != null && text.length() > 0) {
    	TokenStream stream = tokenizer(text);
    	TermAttribute termAttribute = stream.getAttribute(TermAttribute.class);
    	TypeAttribute typeAttribute = stream.getAttribute(TypeAttribute.class);
    	while (stream.incrementToken()) {
    		if (typeAttribute.type().equals(StandardTokenizer.TOKEN_TYPES[StandardTokenizer.ALPHANUM])) {
    			String term = termAttribute.term();
    			if (MOSTLY_ALPHA.matcher(term).matches()) {
    				total++;
    				Integer termCount = terms.get(term);
    				if (termCount == null) {
    					termCount = 1;
    				} else {
    					termCount += 1;
    				}
    				terms.put(term, termCount);
    			}
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

package edu.ipfw.cs575.mobilenews.data;

import com.vividsolutions.jts.geom.Point;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2011-03-26T20:03:42.006-0400")
@StaticMetamodel(Cluster.class)
public class Cluster_ extends NewsEntity_ {
	public static volatile SingularAttribute<Cluster, String> title;
	public static volatile SingularAttribute<Cluster, Point> location;
	public static volatile SingularAttribute<Cluster, String> where;
	public static volatile SingularAttribute<Cluster, Long> lastPublished;
	public static volatile SingularAttribute<Cluster, Long> centroid;
	public static volatile SetAttribute<Cluster, ClusterTerm> terms;
	public static volatile SetAttribute<Cluster, Article> articles;
}

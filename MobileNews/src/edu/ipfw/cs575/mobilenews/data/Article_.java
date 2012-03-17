package edu.ipfw.cs575.mobilenews.data;

import com.vividsolutions.jts.geom.Point;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2011-03-26T16:16:52.180-0400")
@StaticMetamodel(Article.class)
public class Article_ extends WebResource_ {
	public static volatile SingularAttribute<Article, Feed> source;
	public static volatile SingularAttribute<Article, Cluster> cluster;
	public static volatile SingularAttribute<Article, String> where;
	public static volatile SingularAttribute<Article, Point> location;
}

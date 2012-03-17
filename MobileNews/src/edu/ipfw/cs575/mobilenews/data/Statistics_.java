package edu.ipfw.cs575.mobilenews.data;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2011-03-27T18:10:15.018-0400")
@StaticMetamodel(Statistics.class)
public class Statistics_ {
	public static volatile SingularAttribute<Statistics, Long> id;
	public static volatile SingularAttribute<Statistics, Integer> version;
	public static volatile SingularAttribute<Statistics, Integer> articleCount;
	public static volatile SingularAttribute<Statistics, Double> timeAttenuationConstant;
	public static volatile SingularAttribute<Statistics, Double> cutoffDistance;
	public static volatile SingularAttribute<Statistics, Double> locationCutoffDistance;
	public static volatile SingularAttribute<Statistics, Integer> maxTerms;
	public static volatile SingularAttribute<Statistics, Integer> maxTopTerms;
	public static volatile SingularAttribute<Statistics, Integer> maxClusterTimeCentroid;
}

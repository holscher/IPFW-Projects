package edu.ipfw.cs575.mobilenews.data;

import edu.ipfw.cs575.mobilenews.data.ClusterTerm.ClusterTermPK;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2011-03-26T15:16:47.249-0400")
@StaticMetamodel(ClusterTerm.class)
public class ClusterTerm_ {
	public static volatile SingularAttribute<ClusterTerm, ClusterTermPK> clusterKey;
	public static volatile SingularAttribute<ClusterTerm, Integer> version;
	public static volatile SingularAttribute<ClusterTerm, Integer> termCount;
	public static volatile SingularAttribute<ClusterTerm, Double> tfidf;

	@StaticMetamodel(ClusterTermPK.class)
	public static class ClusterTermPK_ {
		public static volatile SingularAttribute<ClusterTermPK, String> term;
		public static volatile SingularAttribute<ClusterTermPK, Cluster> cluster;
	}
}

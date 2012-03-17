package edu.ipfw.cs575.mobilenews.data;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2011-03-20T15:37:47.235-0400")
@StaticMetamodel(WebResource.class)
public class WebResource_ extends NewsEntity_ {
	public static volatile SingularAttribute<WebResource, String> url;
	public static volatile SingularAttribute<WebResource, String> title;
	public static volatile SingularAttribute<WebResource, String> eTag;
	public static volatile SingularAttribute<WebResource, String> lastModified;
	public static volatile SingularAttribute<WebResource, Long> lastScanned;
	public static volatile SingularAttribute<WebResource, Long> lastPublished;
	public static volatile SingularAttribute<WebResource, Long> created;
	public static volatile SingularAttribute<WebResource, Long> updated;
}

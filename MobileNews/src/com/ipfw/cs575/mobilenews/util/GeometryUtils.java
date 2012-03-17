package com.ipfw.cs575.mobilenews.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

final public class GeometryUtils {
	public static final GeometryFactory GEOMETRY = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);

	public static Point createPoint(double longitude, double latitude) {
		return GeometryUtils.GEOMETRY.createPoint(new Coordinate(longitude, latitude));
	}

	
}

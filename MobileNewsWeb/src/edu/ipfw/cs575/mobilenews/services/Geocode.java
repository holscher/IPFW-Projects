package edu.ipfw.cs575.mobilenews.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.ipfw.cs575.mobilenews.util.Geocoder;

/**
 * Session Bean implementation class RevserseGeocode
 */
@Path("geocode")
public class Geocode {

  @GET
  @Path("reverse/{lon},{lat}")
    public String getAddress(@PathParam("lon") double longitude, @PathParam("lat") double latitude) {
	  return Geocoder.reverseGeocodeToName(longitude, latitude);
	}
}

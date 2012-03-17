package com.ipfw.cs575.mobilenews.util;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.microsoft.schemas.search.local.ws.rest.v1.Response;

public interface BingLocation {

	@GET
	@Path("/Locations/{query}")
	Response getLocation(@PathParam("query") String query, @QueryParam("key") String key, @QueryParam("o") String format);

	@GET
	@Path("/Locations/{latitude},{longitude}")
	Response getAddress(@PathParam("longitude") double longitude, @PathParam("latitude") double latitude, @QueryParam("key") String key, @QueryParam("o") String format);
}

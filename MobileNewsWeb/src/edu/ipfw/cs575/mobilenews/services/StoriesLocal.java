package edu.ipfw.cs575.mobilenews.services;

import javax.ejb.Local;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import edu.ipfw.cs575.mobilenews.services.data.NewsNear;

@Local
@Path("/stories")
public interface StoriesLocal {

  @GET
  @Path("/neighbors/{lon},{lat}")
  @Produces("application/json")
  public NewsNear getNeighborsWithin(@PathParam("lon") double longitude, @PathParam("lat") double latitude,
      @DefaultValue("20") @QueryParam("max") int maxNeighbors,
      @DefaultValue("20") @QueryParam("maxAge") int maxAge);
}
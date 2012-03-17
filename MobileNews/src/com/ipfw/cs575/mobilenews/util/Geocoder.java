package com.ipfw.cs575.mobilenews.util;

import java.util.List;

import javax.xml.bind.JAXBContext;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.microsoft.schemas.search.local.ws.rest.v1.Location;
import com.microsoft.schemas.search.local.ws.rest.v1.Resource;
import com.microsoft.schemas.search.local.ws.rest.v1.ResourceSet;
import com.microsoft.schemas.search.local.ws.rest.v1.Response;
import com.vividsolutions.jts.geom.Point;

public class Geocoder {
  private static final Logger log = Logger.getLogger(Geocoder.class);

//  private static final String GEONAMES_USERNAME = "holscher";
  private static final String BING_URL = "http://dev.virtualearth.net/REST/v1";
  public static final String BING_KEY = "AsLi4vfAcChAMuLFL36XOUceNXaxrv7M6KsQWNACOmMj8zt9Zlzw9q8GYCiAWI2y";

  public static final Point geocodeAddress(String address) {

    BingLocation client = ProxyFactory.create(BingLocation.class, BING_URL);

    Response resp = client.getLocation(address, BING_KEY, "xml");
    Location loc = (Location) resp.getResourceSets().getResourceSet().get(0).getResources()
        .getRouteOrDataflowJobOrBirdseyeMetadata().get(0);
    com.microsoft.schemas.search.local.ws.rest.v1.Point point = loc.getPoint();
    return GeometryUtils.createPoint(point.getLongitude(), point.getLatitude());
  }

//  public static final String reverseGeocodeToName(double longitude, double latitude) {
//    try {
//      WebService.setUserName(GEONAMES_USERNAME);
//      Address address = WebService.findNearestAddress(latitude, longitude);
//      return address.toString();
//    } catch (Exception e) {
//      log.warn("Unable to get place name.", e);
//    }
//    return "Unable to determine address.<br>Latitude: " + latitude + " Longitude: " + longitude;
//  }

  public static final String reverseGeocodeToName(double longitude, double latitude) {
    Response resp = null;
    try {
      // this initialization only needs to be done once per VM
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      BingLocation client = ProxyFactory.create(BingLocation.class, BING_URL);
      resp = client.getAddress(longitude, latitude, BING_KEY, "xml");
      List<ResourceSet> resourceSet = resp.getResourceSets().getResourceSet();
      if (resourceSet.size() > 0){
        List<Resource> metadata = resourceSet.get(0).getResources()
        .getRouteOrDataflowJobOrBirdseyeMetadata();
        if (metadata.size() > 0) {
          Location loc = (Location) metadata.get(0);
          String name = loc.getName();
          if (name == null || name.length() == 0) {
            name = loc.getAddress().getFormattedAddress();
          }
          if (name != null && name.length() > 0) {
            return name;
          }
        }
      }
    } catch (Exception e) {
      try {
        JAXBContext.newInstance(Response.class).createMarshaller().marshal(resp, System.out);
      } catch (Exception e1) {
      }
      log.warn("Unable to get place name.", e);
    }
    return "Unable to determine address.<br>Latitude: " + latitude + " Longitude: " + longitude;
  }

}

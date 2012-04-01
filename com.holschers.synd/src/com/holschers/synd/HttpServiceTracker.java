package com.holschers.synd;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class HttpServiceTracker extends ServiceTracker<HttpService, FeedServlet> {

	public HttpServiceTracker(BundleContext context) {
		super(context, HttpService.class, null);
	}
	
	@Override
	public FeedServlet addingService(ServiceReference<HttpService> reference) {
		HttpService httpService = context.getService(reference);
		FeedServlet servlet = new FeedServlet();
		try {
			httpService.registerServlet("/feed", servlet, null, null);
		} catch (Exception e) {
			// TODO Use logger service
		}
		return servlet;
	}
	
	@Override
	public void removedService(ServiceReference<HttpService> reference,
			FeedServlet service) {
		service.destroy();
		context.ungetService(reference);
	}

}

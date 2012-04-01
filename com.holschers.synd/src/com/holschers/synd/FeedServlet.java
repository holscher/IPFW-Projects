package com.holschers.synd;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.feed.synd.SyndPersonImpl;
import com.sun.syndication.io.WireFeedOutput;

public class FeedServlet extends HttpServlet {

	private static final String DEFAULT_FEED_TYPE = "atom_1.0";
	static final String MIMETYPE_ATOM = "application/atom+xml";
	static final String MIMETYPE_RSS = "application/rss+xml";
	static final String MIMETYPE_UNKNOWN = "text/xml";
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	// Due to lists
	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		SyndPerson author = new SyndPersonImpl();
		author.setEmail("editor@example.org");
		author.setName("Example Person");

		SyndFeed feed = new SyndFeedImpl();
		feed.setTitle("Example Feed Output from ROME");
		feed.setDescription("The Example Organization web site");
		feed.setAuthors(Collections.singletonList(author));
		feed.setLink("http://www.example.org/");
		feed.setPublishedDate(new Date());

		SyndEntry entry = new SyndEntryImpl();
		entry.setTitle("First Entry Title");
		entry.setLink("http://www.example.org/item1");
		SyndContent description = new SyndContentImpl();
		description.setValue("News about the Example project");
		description.setType("text");
		entry.setDescription(description);
		entry.setAuthors(Collections.singletonList(author));
		feed.getEntries().add(entry);

		entry = new SyndEntryImpl();
		entry.setTitle("Second Entry Title");
		entry.setLink("http://www.example.org/item2");
		description = new SyndContentImpl();
		description.setValue("<i>More</i> news about the Example project");
		description.setType("html");
		entry.setDescription(description);
		entry.setAuthors(Collections.singletonList(author));
		feed.getEntries().add(entry);

		List<String> supportedTypes = feed.getSupportedFeedTypes();

		String feedType = request.getParameter("feedType");
		if (feedType == null || feedType.length() == 0) {
			feedType = DEFAULT_FEED_TYPE;
		}
		if (!supportedTypes.contains(feedType)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Feed Type '" + feedType
							+ "' is not supported. Use one of "
							+ supportedTypes.toString());
		} else {
			WireFeed wireFeed = feed.createWireFeed(feedType);

			WireFeedOutput output = new WireFeedOutput();
			try {
				response.setContentType(getMimeType(feedType));
				output.output(wireFeed, response.getWriter());
			} catch (Exception e) {
				// TODO: Logger service
			}
		}
	}

	private String getMimeType(String feedType) {
		if (feedType.startsWith("atom_")) {
			return MIMETYPE_ATOM;
		} else if (feedType.startsWith("rss_")) {
			return MIMETYPE_RSS;
		}
		return MIMETYPE_UNKNOWN;
	}

}

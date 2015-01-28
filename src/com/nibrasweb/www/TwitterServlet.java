package com.nibrasweb.www;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterServlet extends HttpServlet {

	private static final long serialVersionUID = -4150769712023470152L;

	private static String regex = "((https?|ftp|file):\\/\\/[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Twitter twitter = TwitterFactory.getSingleton();
		String output = "";
		try {
			List<Status> statuses = twitter.getUserTimeline();

			for (int i = 0; i < statuses.size() && i < 3; i++) {
				Status s = statuses.get(i);
				output += "<li><span>" + wrapLinks(s.getText())
						+ "</span><b><a href=\"" + getUrl(s) + "\">"
						+ getDaysAgo(s) + "</a></b></li>";
			}

			resp.setContentType("text/plain");
			resp.getWriter().println(output);
		} catch (TwitterException e) {
			resp.getWriter().println(e.getErrorMessage());
			throw new RuntimeException(e);
		}

		/*
		 * <li><span> This is Photoshop's version of Lorem Ipsum. Proin gravida
		 * nibh vel velit auctor aliquet. Aenean sollicitudin, lorem quis
		 * bibendum auctor, nisi elit consequat ipsum <a
		 * href="#">http://t.co/CGIrdxIlI3</a> </span> <b><a href="#">2 Days
		 * Ago</a></b></li>
		 */
	}

	private String wrapLinks(String text) {
		return text.replaceAll(regex, "<a href=\"$1\">$1</a>");

	}

	private String getUrl(Status status) {
		return "https://twitter.com/" + status.getUser().getScreenName()
				+ "/status/" + status.getId();
	}

	private String getDaysAgo(Status s) {
		long days = 0;
		Date posted = s.getCreatedAt();
		Date today = new Date();

		long diff = today.getTime() - posted.getTime();

		days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		if (days == 0)
			return "Today";
		else if (days == 1)
			return "Yesterday";
		else
			return Long.toString(days) + " Days Ago";

	}
}

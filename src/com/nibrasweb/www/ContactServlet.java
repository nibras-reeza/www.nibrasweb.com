package com.nibrasweb.www;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ContactServlet extends HttpServlet {

	private static final long serialVersionUID = -816076800806258221L;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/plain");

		String name = req.getParameter("contactName");
		String email = req.getParameter("contactEmail");
		String subject = req.getParameter("contactSubject");
		String message = req.getParameter("contactMessage");

		if (subject == null || subject.equals(""))
			subject = "Contact Form Submission";

		String error = "";
		if (name == null || name.length() < 2)
			error += "Please enter your name\n.";
		if (email == null || email.length() < 5
				|| !pattern.matcher(email).matches())
			error += "Please enter a valid email address.\n";
		if (message == null || message.length() < 8)
			error += "Your message seems too short.\n";

		if (error.length() > 0) {
			resp.getWriter().println(error);
			return;
		}

		try {
			Message msg = new MimeMessage(Session.getDefaultInstance(
					new Properties(), null));
			msg.setFrom(new InternetAddress("nibras.reeza@gmail.com", name));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					"mail@nibrasweb.com", "Nibras Reeza"));
			msg.setSubject(subject);
			msg.setText("From: " + email + "\n\n" + message);
			Transport.send(msg);

		} catch (Exception e) {
			resp.getWriter().println("Something went wrong. Please try again.");
			throw new RuntimeException(e);
		}

		resp.getWriter().print("OK");

	}

}

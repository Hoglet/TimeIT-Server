package se.solit.timeit.application;

import javax.mail.internet.InternetAddress;

public class Email
{

	public Email(InternetAddress recipient, String subject, String message)
	{
		this.recipient = recipient;
		this.subject = subject;
		this.message = message;
	}

	// SONAR:OFF
	public final InternetAddress	recipient;
	public final String				subject;
	public final String				message;
	// SONAR:ON
}

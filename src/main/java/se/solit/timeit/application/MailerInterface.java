package se.solit.timeit.application;

import javax.mail.MessagingException;

import se.solit.timeit.utilities.Email;

public interface MailerInterface
{
	public void sendMail(Email email) throws MessagingException;
}

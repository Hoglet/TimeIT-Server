package se.solit.timeit.application;

import javax.mail.MessagingException;

public interface MailerInterface
{
	public void sendMail(Email email) throws MessagingException;
}

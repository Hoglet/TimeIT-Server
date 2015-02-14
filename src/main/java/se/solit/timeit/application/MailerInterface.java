package se.solit.timeit.application;

import javax.mail.MessagingException;

import Utilities.Email;

public interface MailerInterface
{
	public void sendMail(Email email) throws MessagingException;
}

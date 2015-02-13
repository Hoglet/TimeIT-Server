package se.solit.timeit.application;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer implements MailerInterface
{

	private final InternetAddress	from;
	private final Properties		properties;

	public Mailer(String mailserver) throws AddressException
	{
		properties = System.getProperties();
		properties.setProperty("mail.smtp.host", mailserver);
		properties.setProperty("mail.smtp.auth", "false");
		properties.setProperty("mail.smtp.starttls.enable", "false");
		properties.setProperty("mail.smtp.port", "25");
		this.from = new InternetAddress("hostmaster@localhost");
	}

	@Override
	public void sendMail(Email email) throws MessagingException
	{
		Session session = Session.getDefaultInstance(properties);
		MimeMessage message = new MimeMessage(session);

		message.setFrom(from);
		message.addRecipient(Message.RecipientType.TO, email.recipient);
		message.setText(email.message);
		message.setSubject(email.subject);
		Transport.send(message);
	}

}

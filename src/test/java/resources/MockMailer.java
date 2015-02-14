package resources;

import Utilities.Email;
import se.solit.timeit.application.MailerInterface;

public class MockMailer implements MailerInterface
{
	private Email	email;

	@Override
	public void sendMail(Email email)
	{
		this.email = email;
	}

	public Email getEmail()
	{
		return email;
	}

}

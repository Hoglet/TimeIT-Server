package resources;

import se.solit.timeit.application.MailerInterface;
import se.solit.timeit.utilities.Email;

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

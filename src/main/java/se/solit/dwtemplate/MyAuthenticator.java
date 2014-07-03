package se.solit.dwtemplate;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import javax.persistence.EntityManagerFactory;

import se.solit.dwtemplate.dao.UserDAO;
import se.solit.dwteplate.entities.User;

import com.google.common.base.Optional;

public class MyAuthenticator implements Authenticator<BasicCredentials, User>
{
	private final EntityManagerFactory	emf;

	public MyAuthenticator(EntityManagerFactory entityManagerFactory)
	{
		emf = entityManagerFactory;
	}

	@Override
	public Optional<User> authenticate(BasicCredentials credentials)
			throws AuthenticationException
	{
		UserDAO userDAO = new UserDAO(emf);
		User user = userDAO.getUser(credentials.getUsername());
		if (user != null && user.getPassword().equals(credentials.getPassword()))
		{
			return Optional.of(user);
		}
		return Optional.absent();
	}
}

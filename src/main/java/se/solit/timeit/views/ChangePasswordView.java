package se.solit.timeit.views;

import javax.servlet.http.HttpSession;

import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class ChangePasswordView extends BaseView
{
	private final String	temporaryKey;

	public ChangePasswordView(User user, String temporaryKey, HttpContext context,
			HttpSession session)
	{
		super("passwordChange.ftl", user, context, session);
		this.temporaryKey = temporaryKey;
	}

	public User getUser()
	{
		return user;
	}

	public String getTemporaryKey()
	{
		return temporaryKey;
	}

}

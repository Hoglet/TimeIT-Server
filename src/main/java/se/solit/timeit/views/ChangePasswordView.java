package se.solit.timeit.views;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriInfo;

import com.sun.net.httpserver.HttpContext;
import se.solit.timeit.entities.User;

public class ChangePasswordView extends BaseView
{
	private final String	temporaryKey;

	public ChangePasswordView(User user, String temporaryKey, UriInfo uriInfo,
			HttpSession session)
	{
		super("passwordChange.ftl", user, uriInfo, session);
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

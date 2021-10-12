package se.solit.timeit.views;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriInfo;

import com.sun.net.httpserver.HttpContext;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;

public class DeleteUserView extends BaseView
{
	private final User	user2Delete;

	public DeleteUserView(EntityManagerFactory emf, User authorizedUser, String username, UriInfo uriInfo,
			HttpSession session)
	{
		super("userDelete.ftl", authorizedUser, uriInfo, session);
		UserDAO userDAO = new UserDAO(emf);
		user2Delete = userDAO.getUser(username);
	}

	public User getUser()
	{
		return user2Delete;
	}

}

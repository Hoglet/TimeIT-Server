package se.solit.timeit.views;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriInfo;

import com.sun.net.httpserver.HttpContext;
import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;

public class UserAddView extends BaseView
{
	private final RoleDAO	roleDAO;

	public UserAddView(EntityManagerFactory emf, User user, UriInfo uriInfo, HttpSession session)
	{
		super("useradd.ftl", user, uriInfo, session);
		roleDAO = new RoleDAO(emf);
	}

	public Collection<Role> getRoles()
	{
		return roleDAO.getRoles();
	}
}

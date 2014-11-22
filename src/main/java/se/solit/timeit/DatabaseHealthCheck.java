package se.solit.timeit;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.entities.Role;

import com.codahale.metrics.health.HealthCheck;

public class DatabaseHealthCheck extends HealthCheck
{

	private final RoleDAO	roleDAO;

	public DatabaseHealthCheck(EntityManagerFactory emf)
	{
		roleDAO = new RoleDAO(emf);
	}

	@Override
	public Result check() throws Exception
	{
		Collection<Role> roles = roleDAO.getRoles();
		if (roles.isEmpty())
		{
			return Result.unhealthy("No roles in database");
		}
		return Result.healthy();
	}
}

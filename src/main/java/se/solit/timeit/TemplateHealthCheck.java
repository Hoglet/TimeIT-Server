package se.solit.timeit;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.entities.Role;

import com.codahale.metrics.health.HealthCheck;

public class TemplateHealthCheck extends HealthCheck
{
	EntityManagerFactory	emf;

	public TemplateHealthCheck(EntityManagerFactory entityManagerFactory)
	{
		emf = entityManagerFactory;
	}

	@Override
	protected Result check() throws Exception
	{
		RoleDAO roleDAO = new RoleDAO(emf);
		Collection<Role> roles = roleDAO.getRoles();

		if (roles.isEmpty())
		{
			return Result.unhealthy("template doesn't include a name");
		}
		return Result.healthy();
	}
}

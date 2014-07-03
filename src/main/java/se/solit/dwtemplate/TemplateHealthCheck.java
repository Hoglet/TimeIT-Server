package se.solit.dwtemplate;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.dwtemplate.dao.RoleDAO;
import se.solit.dwteplate.entities.Role;

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

		if (roles.size() < 1)
		{
			return Result.unhealthy("template doesn't include a name");
		}
		return Result.healthy();
	}
}

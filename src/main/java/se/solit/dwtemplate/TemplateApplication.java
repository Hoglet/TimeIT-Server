package se.solit.dwtemplate;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import se.solit.dwtemplate.dao.RoleDAO;
import se.solit.dwtemplate.resources.AdminResource;
import se.solit.dwtemplate.resources.IndexResource;
import se.solit.dwteplate.entities.Role;
import se.solit.dwteplate.entities.User;

public class TemplateApplication extends Application<TemplateConfiguration>
{

	private static String[]	args	= { "server", "./src/main/config/template-config.yml" };

	@Override
	public String getName()
	{
		return "dropwizard-template";
	}

	@Override
	public void initialize(Bootstrap<TemplateConfiguration> bootstrap)
	{
		bootstrap.addBundle(new ViewBundle());
		bootstrap.addBundle(new AssetsBundle("/assets", "/assets"));
	}

	@Override
	public void run(TemplateConfiguration configuration, Environment environment)
	{
		EntityManagerFactory entityManagerFactory = createJpaPersistFactory(configuration.getDatabase());

		// environment.jersey().getResourceConfig().getContainerRequestFilters().add(new AuthenticationFilter());

		// filter.addMappingForServletNames();

		// *** Dropwizard 6.0 **
		// environment.addFilter(injector.getInstance(PersistFilter.class), "/*");

		environment.jersey().register(new IndexResource());
		environment.jersey().register(new AdminResource(entityManagerFactory));
		environment.jersey().register(
				new BasicAuthProvider<User>(new MyAuthenticator(entityManagerFactory), "Authenticator"));
	}

	private EntityManagerFactory createJpaPersistFactory(DatabaseConfiguration conf)
	{
		Map<String, String> props = new HashMap<String, String>();
		props.put("javax.persistence.jdbc.url", conf.getUrl());
		props.put("javax.persistence.jdbc.user", conf.getUser());
		props.put("javax.persistence.jdbc.password", conf.getPassword());
		props.put("javax.persistence.jdbc.driver", conf.getDriverClass());
		props.put("javax.persistence.schema-generation.database.action", "create");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Default", props);
		populateTables(emf);
		return emf;
	}

	private void populateTables(EntityManagerFactory emf)
	{
		RoleDAO roleDAO = new RoleDAO(emf);
		if (roleDAO.get("Admin") == null)
		{
			Role role = new Role("Admin");
			roleDAO.add(role);
		}
	}

	public static void main(String[] op_args) throws Exception
	{
		if (op_args.length > 0)
		{
			args = op_args;
		}
		new TemplateApplication().run(args);
	}
}

//TODO: 1. Health checks
//TODO: 2. Error management and logging...
//TODO: 3. DB with settings (version of db for upgrades)
//TODO: 4. Warning to users with old browsers (IE9 and older)
//TODO: 5. Unit tests checking access

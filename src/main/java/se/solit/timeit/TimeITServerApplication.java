package se.solit.timeit;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.jersey.sessions.HttpSessionProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.jetty.server.session.SessionHandler;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.AdminResource;
import se.solit.timeit.resources.IndexResource;
import se.solit.timeit.resources.SyncResource;
import se.solit.timeit.serializers.TaskDeSerializer;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class TimeITServerApplication extends Application<TimeITConfiguration>
{

	private static String[]	args	= { "server", "./src/main/config/template-config.yml" };

	@Override
	public String getName()
	{
		return "dropwizard-template";
	}

	@Override
	public void initialize(Bootstrap<TimeITConfiguration> bootstrap)
	{
		bootstrap.addBundle(new ViewBundle());
		bootstrap.addBundle(new AssetsBundle("/assets", "/assets"));
	}

	@Override
	public void run(TimeITConfiguration configuration, Environment environment)
	{
		EntityManagerFactory emf = createJpaPersistFactory(configuration.getDatabase());

		final TemplateHealthCheck healthCheck = new TemplateHealthCheck(emf);
		environment.healthChecks().register("databases", healthCheck);

		SimpleModule module = new SimpleModule("MyModule");
		module.addDeserializer(Task.class, new TaskDeSerializer(emf));
		environment.getObjectMapper().registerModule(module);

		environment.jersey().register(HttpSessionProvider.class);
		environment.servlets().setSessionHandler(new SessionHandler());

		/*		UserDAO userDAO = new UserDAO(emf);

				User user = userDAO.getUser("tester");

				TaskDAO taskDAO = new TaskDAO(emf);
				Task task = new Task(UUID.randomUUID().toString(), "Test", "", false, 0, false, user);
				taskDAO.add(task);
		*/

		environment.jersey().register(new IndexResource());
		environment.jersey().register(new SyncResource(emf));
		environment.jersey().register(new AdminResource(emf));
		environment.jersey().register(
				new BasicAuthProvider<User>(new MyAuthenticator(emf), "Authenticator"));
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

	public static void main(String[] opArgs)
	{
		if (opArgs.length > 0)
		{
			args = opArgs;
		}
		try
		{
			new TimeITServerApplication().run(args);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
}

//TODO: Unit tests checking access

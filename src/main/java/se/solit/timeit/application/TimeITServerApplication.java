package se.solit.timeit.application;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.jersey.sessions.HttpSessionProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import javax.mail.internet.AddressException;
import javax.persistence.EntityManagerFactory;

import org.eclipse.jetty.server.session.SessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.solit.timeit.entities.User;
import se.solit.timeit.resources.CredentialRecoveryResource;
import se.solit.timeit.resources.IndexResource;
import se.solit.timeit.resources.ReportResource;
import se.solit.timeit.resources.TaskResource;
import se.solit.timeit.resources.TasksSyncResource;
import se.solit.timeit.resources.TimeResource;
import se.solit.timeit.resources.TimesSyncResource;
import se.solit.timeit.resources.UserResource;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class TimeITServerApplication extends Application<TimeITConfiguration>
{

	private static String[]		args	= { "server", "./src/main/config/template-config.yml" };
	private static final Logger	LOGGER	= LoggerFactory.getLogger(TimeITServerApplication.class);

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
	public void run(TimeITConfiguration configuration, Environment environment) throws AddressException
	{
		Database db = new Database(configuration.getDatabase());
		EntityManagerFactory emf = db.createJpaPersistFactory();

		final DatabaseHealthCheck healthCheck = new DatabaseHealthCheck(emf);
		environment.healthChecks().register("databases", healthCheck);

		SimpleModule module = new SimpleModule("MyModule");
		environment.getObjectMapper().registerModule(module);

		environment.jersey().register(HttpSessionProvider.class);
		environment.servlets().setSessionHandler(new SessionHandler());

		String mailserver = configuration.getMailserver();
		MailerInterface mailer = new Mailer(mailserver);

		environment.jersey().register(new CredentialRecoveryResource(emf, mailer));
		environment.jersey().register(new IndexResource(emf));
		environment.jersey().register(new TaskResource(emf));
		environment.jersey().register(new TimeResource(emf));
		environment.jersey().register(new TasksSyncResource(emf));
		environment.jersey().register(new TimesSyncResource(emf));
		environment.jersey().register(new UserResource(emf));
		environment.jersey().register(new BasicAuthProvider<User>(new MyAuthenticator(emf), "Authenticator"));
		environment.jersey().register(new ReportResource(emf));
		environment.jersey().register(HttpSessionProvider.class);
		environment.servlets().setSessionHandler(new SessionHandler());
	}

	// SONAR:OFF
	public static void main(String[] opArgs) throws Exception
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
			LOGGER.error(e.toString());
			throw e;
		}
	}
	// SONAR:ON
}

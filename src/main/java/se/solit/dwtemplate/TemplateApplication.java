package se.solit.dwtemplate;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import se.solit.dwtemplate.resources.AdminResource;
import se.solit.dwtemplate.resources.IndexResource;

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
	}

	/*	private JpaPersistModule createJpaPersistModule(DatabaseConfiguration conf)
		{
			Properties props = new Properties();
			props.put("javax.persistence.jdbc.url", conf.getUrl());
			props.put("javax.persistence.jdbc.user", conf.getUser());
			props.put("javax.persistence.jdbc.password", conf.getPassword());
			props.put("javax.persistence.jdbc.driver", conf.getDriverClass());
			JpaPersistModule jpaModule = new JpaPersistModule("Default");
			jpaModule.properties(props);
			return jpaModule;
		}
	*/
	private EntityManagerFactory createJpaPersistFactory(DatabaseConfiguration conf)
	{
		Map<String, String> props = new HashMap<String, String>();
		props.put("javax.persistence.jdbc.url", conf.getUrl());
		props.put("javax.persistence.jdbc.user", conf.getUser());
		props.put("javax.persistence.jdbc.password", conf.getPassword());
		props.put("javax.persistence.jdbc.driver", conf.getDriverClass());
		props.put("javax.persistence.schema-generation.database.action", "create");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Default", props);
		createTables(emf);
		return emf;
	}

	private void createTables(EntityManagerFactory emf)
	{
		/*		EntityManager em = emf.createEntityManager();
				em.createQuery(
						"CREATE TABLE IF NOT EXISTS users"
								+ "( username VARCHAR(40) PRIMARY KEY,"
								+ " name VARCHAR(120) NOT NULL,"
								+ " email VARCHAR(75) NOT NULL,"
								+ " password VARCHAR(75) NOT NULL);").executeUpdate();
				em.close();*/
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

//TODO: 1. UI för att ta bort användare
//TODO: 1.1 (check that user is selected and show/hide edit &delete buttons)
//TODO: 1.2 actually delete the user.
//TODO: 2. Roller i databasen
//TODO: 3. UI Lägga till roller på användare
//TODO: 4. Säkerhet, Enbart admin får komma åt/se "admin"
//TODO: 4. Health checks
//TODO: 5. Warning to users with old browsers (IE9 and older)


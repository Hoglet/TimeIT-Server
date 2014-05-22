package se.solit.dwtemplate;

import se.solit.dwtemplate.resources.AdminResource;
import se.solit.dwtemplate.resources.IndexResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class TemplateApplication extends Application<TemplateConfiguration>
{

	private static String[] args = {"server", "./src/main/config/template-config.yml" };

	@Override
	public String getName()
	{
		return "dropwizard-template";
	}

	@Override
	public void initialize(Bootstrap<TemplateConfiguration> bootstrap)
	{
	    bootstrap.addBundle(new ViewBundle());
	    bootstrap.addBundle(new AssetsBundle("/assets","/assets"));
	}

	@Override
	public void run(TemplateConfiguration configuration, Environment environment)
	{
		environment.jersey().register(new IndexResource());
		environment.jersey().register(new AdminResource());
	}

	public static void main(String[] op_args) throws Exception
	{
		if(op_args.length>0)
		{
			args= op_args;
		}
		new TemplateApplication().run(args);
	}
}

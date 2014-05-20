package se.nineeyes.templatedw;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TemplateApplication extends Application<TemplateConfiguration> {

	private final TemplateResources resources;
	private final String[] args;

	@Override
	public String getName() {
		return "dropwizard-template";
	}

	@Override
	public void initialize(Bootstrap<TemplateConfiguration> bootstrap) {
	}

	@Override
	public void run(TemplateConfiguration configuration, Environment environment) {
		resources.run(configuration, environment);
	}

	public static void main(String[] args) throws Exception {
		TemplateApplication app =  new TemplateApplication().run(args);
	}
}

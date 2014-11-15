package se.solit.timeit;

import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimeITConfiguration extends Configuration
{
	@NotEmpty
	private String					template;

	@NotEmpty
	private String					defaultName	= "Stranger";

	@NotNull
	@Valid
	private DatabaseConfiguration	database;

	@JsonProperty
	public String getTemplate()
	{
		return template;
	}

	@JsonProperty
	public void setTemplate(String template)
	{
		this.template = template;
	}

	@JsonProperty
	public String getDefaultName()
	{
		return defaultName;
	}

	@JsonProperty
	public void setDefaultName(String name)
	{
		this.defaultName = name;
	}

	public DatabaseConfiguration getDatabase()
	{
		return database;
	}

	public void setDatabase(DatabaseConfiguration database)
	{
		this.database = database;
	}

}

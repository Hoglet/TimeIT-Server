package se.solit.timeit.application;

import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimeITConfiguration extends Configuration
{
	@NotEmpty
	private String					defaultName	= "Stranger";

	private String					mailserver	= "";

	@NotNull
	@Valid
	private DatabaseConfiguration	database;

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

	public void setMailserver(String name)
	{
		this.mailserver = name;
	}

	public DatabaseConfiguration getDatabase()
	{
		return database;
	}

	public void setDatabase(DatabaseConfiguration database)
	{
		this.database = database;
	}

	public String getMailserver()
	{
		return mailserver;
	}

}

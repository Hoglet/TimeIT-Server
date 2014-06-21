package se.solit.dwtemplate.views;

import com.google.common.base.Charsets;

import io.dropwizard.views.View;

public class UserAddView extends View
{
	public UserAddView()
	{
		super("useradd.ftl", Charsets.UTF_8);
	}
}

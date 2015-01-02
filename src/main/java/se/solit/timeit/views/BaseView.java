package se.solit.timeit.views;

import io.dropwizard.views.View;
import se.solit.timeit.entities.User;

import com.google.common.base.Charsets;

public class BaseView extends View
{

	protected final User	user;

	public BaseView(String template, User user)
	{
		super(template, Charsets.UTF_8);
		this.user = user;
	}

	public User getCurrentUser()
	{
		return user;
	}

}

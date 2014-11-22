package se.solit.timeit.views;

import io.dropwizard.views.View;
import se.solit.timeit.entities.User;

import com.google.common.base.Charsets;

public class IndexView extends View
{
	private final User	user;

	public IndexView(User user2)
	{
		super("index.ftl", Charsets.UTF_8);
		user = user2;
	}

	public User getCurrentUser()
	{
		return user;
	}
}

package se.solit.timeit.views;

import io.dropwizard.views.View;
import se.solit.timeit.entities.User;

import com.google.common.base.Charsets;

public class TaskAddedView extends View
{

	private User	user;

	public TaskAddedView(User user)
	{
		super("taskAdded.ftl", Charsets.UTF_8);
		this.user = user;
	}

	public User getCurrentUser()
	{
		return user;
	}
}

package se.solit.timeit.views;

import io.dropwizard.views.View;
import se.solit.timeit.entities.User;

import com.google.common.base.Charsets;

public class MessageView extends View
{

	private final User		user;
	private final String	headline;
	private final String	url;
	private final String	text;

	public MessageView(User user2, String headline, String text, String url)
	{
		super("message.ftl", Charsets.UTF_8);
		this.user = user2;
		this.text = text;
		this.headline = headline;
		this.url = url;
	}

	public String getHeadline()
	{
		return headline;
	}

	public String getText()
	{
		return text;
	}

	public String getUrl()
	{
		return url;
	}

	public User getCurrentUser()
	{
		return user;
	}
}

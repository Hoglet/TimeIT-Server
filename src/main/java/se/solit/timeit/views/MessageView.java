package se.solit.timeit.views;

import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class MessageView extends BaseView
{
	private final String	headline;
	private final String	url;
	private final String	text;

	public MessageView(User user, String headline, String text, String url, HttpContext context)
	{
		super("message.ftl", user, context);
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

}

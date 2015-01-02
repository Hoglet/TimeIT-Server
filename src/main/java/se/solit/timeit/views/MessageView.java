package se.solit.timeit.views;

import se.solit.timeit.entities.User;

public class MessageView extends BaseView
{
	private final String	headline;
	private final String	url;
	private final String	text;

	public MessageView(User user, String headline, String text, String url)
	{
		super("message.ftl", user);
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

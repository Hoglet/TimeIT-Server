package se.solit.timeit.views;

import io.dropwizard.views.View;

import org.joda.time.DateTime;

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

	public String getReportLink()
	{
		DateTime now = DateTime.now();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<a href='/report/");
		stringBuilder.append(user.getUsername());
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(now.getYear()));
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(now.getMonthOfYear()));
		stringBuilder.append("'>Reports</a>");
		return stringBuilder.toString();
	}

}

package se.solit.timeit.views;

import io.dropwizard.views.View;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.joda.time.DateTime;

import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;

import com.google.common.base.Charsets;
import com.sun.jersey.api.core.HttpContext;

public class BaseView extends View
{

	protected final User				user;

	List<SimpleEntry<String, String>>	list;

	private String						currentPath	= "/";

	public BaseView(String template, User user, HttpContext context)
	{
		super(template, Charsets.UTF_8);
		this.user = user;
		if (context != null)
		{
			currentPath = context.getRequest().getPath();
		}
		list = new ArrayList<SimpleEntry<String, String>>();
		if (user.hasRole(Role.ADMIN))
		{
			list.add(new SimpleEntry<String, String>("user/", "admin"));
		}
		else
		{
			list.add(new SimpleEntry<String, String>("user/", "user"));
		}
		list.add(new SimpleEntry<String, String>("report/", "report"));
	}

	public User getCurrentUser()
	{
		return user;
	}

	public String getReportLink()
	{
		DateTime now = DateTime.now();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<a class=\"");
		stringBuilder.append(getClasses("report"));
		stringBuilder.append("\" ");
		stringBuilder.append(" href='/report/");
		stringBuilder.append(user.getUsername());
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(now.getYear()));
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(now.getMonthOfYear()));
		stringBuilder.append("'>Reports</a>");
		return stringBuilder.toString();
	}

	public String getClasses(String key)
	{
		if (key.equals(getCurrentKey()))
		{
			return "selected";
		}
		return "";
	}

	private String getCurrentKey()
	{
		for (Entry<String, String> e : list)
		{
			if (currentPath.contains(e.getKey()))
			{
				return e.getValue();
			}
		}
		return "home";
	}
}

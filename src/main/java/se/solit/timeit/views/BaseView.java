package se.solit.timeit.views;

import com.sun.net.httpserver.HttpContext;
import io.dropwizard.views.View;

import java.time.ZonedDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriInfo;

import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;

import com.google.common.base.Charsets;

public class BaseView extends View
{

	protected final User				user;

	List<SimpleEntry<String, String>>	list;

	private String						currentPath	= "/";

	private String						message;

	public BaseView(String template, User user, UriInfo uriInfo, HttpSession session)
	{
		super(template, Charsets.UTF_8);
		this.user = user;
		this.message = (String) session.getAttribute("message");
		if (this.message == null)
		{
			this.message = "";
		}
		session.removeAttribute("message");

		if (uriInfo != null)
		{
			currentPath = uriInfo.getPath();
		}
		list = new ArrayList<SimpleEntry<String, String>>();
		if (user != null && user.hasRole(Role.ADMIN))
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
		ZonedDateTime now = ZonedDateTime.now();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<a class=\"");
		stringBuilder.append(getClasses("report"));
		stringBuilder.append("\" ");
		stringBuilder.append(" href='/report/");
		stringBuilder.append(user.getUsername());
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(now.getYear()));
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(now.getMonthValue()));
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

	public String getMessage()
	{
		return message;
	}
}

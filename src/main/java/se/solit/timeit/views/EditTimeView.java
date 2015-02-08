package se.solit.timeit.views;

import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class EditTimeView extends BaseView
{
	private final Time	time;

	public EditTimeView(EntityManagerFactory emf, Time time, User user, HttpContext context, HttpSession session)
	{
		super("editTime.ftl", user, context, session);
		this.time = time;
	}

	public Time getTime()
	{
		return time;
	}

	public String getTaskName() throws SQLException
	{
		return time.getTask().getName();
	}

}

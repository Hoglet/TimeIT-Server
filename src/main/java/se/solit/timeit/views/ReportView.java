package se.solit.timeit.views;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.TimeDescriptor;
import se.solit.timeit.dao.TimeDescriptorList;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class ReportView extends BaseView
{
	private static final int		YEAR_TAB_ID				= 0;
	private static final int		MONTH_TAB_ID			= 1;
	private static final int		DAY_TAB_ID				= 2;
	protected static final int		TWO						= 2;
	protected static final int		MONTHS_IN_YEAR			= 12;
	protected static final int		LAST_SECOND_OF_MINUTE	= 59;
	protected static final int		LAST_MINUTE_OF_HOUR		= 59;
	protected static final int		LAST_HOUR_OF_DAY		= 23;

	protected DateTime				pointInTime;
	protected final User			reportedUser;
	protected TimeDescriptorList	times;
	protected final TimeDAO			timeDAO;

	protected String[]				itemClass;
	protected final List<Task>		tasks					= new ArrayList<Task>();

	public ReportView(String template, User user, DateTime pointInTime, User reportedUser, HttpContext context,
			HttpSession session, EntityManagerFactory emf)
	{
		super(template, user, context, session);
		timeDAO = new TimeDAO(emf);
		this.pointInTime = pointInTime;
		this.reportedUser = reportedUser;
	}

	public void extractTimeDescriptors(DateTime start, DateTime stop)
	{
		times = timeDAO.getTimes(user, start, stop);
		int numberOfItems = times.size();
		assignItemClasses(numberOfItems);
	}

	public void extractTasks()
	{
		for (TimeDescriptor time : times)
		{
			if (time.getDuration().isLongerThan(new Duration(0)))
			{
				tasks.add(time.getTask());
			}
		}
	}

	public String getTaskClass(Task task)
	{
		int index = tasks.indexOf(task);
		String result = "Item ";
		if (index >= 0)
		{
			return result + itemClass[index];
		}
		return result;
	}

	private void assignItemClasses(int numberOfItems)
	{
		itemClass = new String[numberOfItems];
		for (int c = 0; c < numberOfItems; c++)
		{
			itemClass[c] = "Item" + c;
		}
	}

	public String tabs(int id)
	{
		int year = pointInTime.getYear();
		int month = pointInTime.getMonthOfYear();
		int day = pointInTime.getDayOfYear();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<div class='tabs'>");
		yearTab(id, year, stringBuilder);
		monthTab(id, year, month, stringBuilder);
		dayTab(id, year, month, day, stringBuilder);
		stringBuilder.append("</div>");
		return stringBuilder.toString();
	}

	private void yearTab(int id, int year, StringBuilder stringBuilder)
	{
		if (id != YEAR_TAB_ID)
		{
			stringBuilder.append("<a href='/report/");
			stringBuilder.append(reportedUser.getUsername());
			stringBuilder.append("/");
			stringBuilder.append(year);
			stringBuilder.append("'>");
			stringBuilder.append("<div class='tab'>");
		}
		else
		{
			stringBuilder.append("<div class='tab selected'>");
		}
		stringBuilder.append("<h2>Year</h2>");
		stringBuilder.append("</div>");
		if (id != YEAR_TAB_ID)
		{
			stringBuilder.append("</a>");
		}
	}

	private void monthTab(int id, int year, int month, StringBuilder stringBuilder)
	{
		if (id != MONTH_TAB_ID)
		{
			stringBuilder.append("<a href='/report/");
			stringBuilder.append(reportedUser.getUsername());
			stringBuilder.append("/");
			stringBuilder.append(year);
			stringBuilder.append("/");
			stringBuilder.append(month);
			stringBuilder.append("'>");
			stringBuilder.append("<div class='tab'>");
		}
		else
		{
			stringBuilder.append("<div class='tab selected'>");
		}
		stringBuilder.append("<h2>Month</h2>");
		stringBuilder.append("</div>");
		if (id != MONTH_TAB_ID)
		{
			stringBuilder.append("</a>");
		}
	}

	private void dayTab(int id, int year, int month, int day, StringBuilder stringBuilder)
	{
		if (id != DAY_TAB_ID)
		{
			stringBuilder.append("<a href='/report/");
			stringBuilder.append(reportedUser.getUsername());
			stringBuilder.append("/");
			stringBuilder.append(year);
			stringBuilder.append("/");
			stringBuilder.append(month);
			stringBuilder.append("/");
			stringBuilder.append(day);
			stringBuilder.append("'>");
			stringBuilder.append("<div class='tab'>");
		}
		else
		{
			stringBuilder.append("<div class='tab selected'>");
		}
		stringBuilder.append("<h2>Day</h2>");
		stringBuilder.append("</div>");
		if (id != DAY_TAB_ID)
		{
			stringBuilder.append("</a>");
		}
	}

	public String getYear()
	{
		return pointInTime.toString("YYYY");
	}
}

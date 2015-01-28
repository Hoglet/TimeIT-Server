package se.solit.timeit.views;

import org.joda.time.DateTime;

import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class ReportView extends BaseView
{
	private static final int	YEAR_TAB_ID				= 0;
	private static final int	MONTH_TAB_ID			= 1;
	private static final int	DAY_TAB_ID				= 2;
	protected static final int	TWO						= 2;
	protected static final int	MONTHS_IN_YEAR			= 12;
	protected static final int	LAST_SECOND_OF_MINUTE	= 59;
	protected static final int	LAST_MINUTE_OF_HOUR		= 59;
	protected static final int	LAST_HOUR_OF_DAY		= 23;

	protected DateTime			pointInTime;
	protected final User		reportedUser;

	public ReportView(String template, User user, DateTime pointInTime, User reportedUser, HttpContext context)
	{
		super(template, user, context);
		this.pointInTime = pointInTime;
		this.reportedUser = reportedUser;
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

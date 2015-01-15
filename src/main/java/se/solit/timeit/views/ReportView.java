package se.solit.timeit.views;

import org.joda.time.DateTime;

import se.solit.timeit.entities.User;

public class ReportView extends BaseView
{
	protected static final int	TWO						= 2;
	protected static final int	MONTHS_IN_YEAR			= 12;
	protected static final int	LAST_SECOND_OF_MINUTE	= 59;
	protected static final int	LAST_MINUTE_OF_HOUR		= 59;
	protected static final int	LAST_HOUR_OF_DAY		= 23;

	protected DateTime			pointInTime;
	protected final User		reportedUser;

	public ReportView(String template, User user, DateTime pointInTime, User reportedUser)
	{
		super(template, user);
		this.pointInTime = pointInTime;
		this.reportedUser = reportedUser;
	}

	public String tabs(int id)
	{
		String year = pointInTime.toString("YYYY");
		String month = pointInTime.toString("M");
		StringBuilder stringBuilder = new StringBuilder();
		monthTab(id, year, month, stringBuilder);
		yearTab(id, year, stringBuilder);
		return stringBuilder.toString();
	}

	private void yearTab(int id, String year, StringBuilder stringBuilder)
	{
		if (id == 0)
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
		stringBuilder.append("<h2>Year report</h2>");
		stringBuilder.append("</div>");
		if (id == 0)
		{
			stringBuilder.append("</a>");
		}
		stringBuilder.append("</div>");
	}

	private void monthTab(int id, String year, String month, StringBuilder stringBuilder)
	{
		stringBuilder.append("<div class='tabs'>");
		if (id == 1)
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
		stringBuilder.append("<h2>Month report</h2>");
		stringBuilder.append("</div>");
		if (id == 1)
		{
			stringBuilder.append("</a>");
		}
	}

	public String getYear()
	{
		return pointInTime.toString("YYYY");
	}
}

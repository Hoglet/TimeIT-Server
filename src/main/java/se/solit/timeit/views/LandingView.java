package se.solit.timeit.views;

import javax.servlet.http.HttpSession;

import com.sun.jersey.api.core.HttpContext;

public class LandingView extends BaseView
{

	public LandingView(HttpContext context, HttpSession session)
	{
		super("landing.ftl", null, context, session);
	}

}

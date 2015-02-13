package se.solit.timeit.views;

import javax.servlet.http.HttpSession;

import com.sun.jersey.api.core.HttpContext;

public class RecoverView extends BaseView
{

	public RecoverView(HttpContext context, HttpSession session)
	{
		super("recover.ftl", null, context, session);
	}

}

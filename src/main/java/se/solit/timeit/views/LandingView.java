package se.solit.timeit.views;

import com.sun.net.httpserver.HttpContext;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriInfo;

public class LandingView extends BaseView
{

	public LandingView(UriInfo uriInfo, HttpSession session)
	{
		super("landing.ftl", null, uriInfo, session);
	}

}

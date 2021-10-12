package se.solit.timeit.views;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriInfo;

import com.sun.net.httpserver.HttpContext;

public class RecoverView extends BaseView
{

	public RecoverView(UriInfo uriInfo, HttpSession session)
	{
		super("recover.ftl", null, uriInfo, session);
	}

}

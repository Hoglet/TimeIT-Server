package se.solit.timeit.resources;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class BaseResource
{

	protected WebApplicationException redirect(String destination) throws URISyntaxException
	{
		URI uri = new URI(destination);
		Response response = Response.seeOther(uri).build();
		return new WebApplicationException(response);
	}

	/**
	 * Set message to show the message in the next shown View.
	 *
	 * @param session
	 * @param message
	 */
	protected void setMessage(HttpSession session, String message)
	{
		session.setAttribute("message", message);
	}

}

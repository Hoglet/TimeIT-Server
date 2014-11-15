package se.solit.timeit;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.google.common.base.Strings;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class AuthenticationFilter implements ContainerRequestFilter
{

	public AuthenticationFilter()
	{
	}

	@Override
	public ContainerRequest filter(final ContainerRequest containerRequest)
	{
		String authenticationToken = "";// containerRequest.getHeaderValue(Constants.HEADER_TOKEN_PARAM_NAME);

		if (Strings.isNullOrEmpty(authenticationToken))
		{
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
		}
		/*
		 * else if (!authenticationDAO.findByAuthenticationToken(authenticationToken).isPresent()) { throw new
		 * WebApplicationException(Response.status(Response.Status.FORBIDDEN).build()); }
		 */
		return containerRequest;
	}
}

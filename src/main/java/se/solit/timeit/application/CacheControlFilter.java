package se.solit.timeit.application;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

public class CacheControlFilter implements Filter
{

	private static final int	CACHE_MAX_AGE	= 500000;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException
	{

		HttpServletResponse resp = (HttpServletResponse) response;

		// Add whatever headers you want here
		resp.setHeader("Cache-Control", "public, max-age=" + CACHE_MAX_AGE);
		resp.setHeader("Expires", DateTime.now().plusSeconds(CACHE_MAX_AGE) + "");

		chain.doFilter(request, response);
	}

	@Override
	public void destroy()
	{
		/* Only needs to exist */
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException
	{
		/* Only needs to exist */
	}

}

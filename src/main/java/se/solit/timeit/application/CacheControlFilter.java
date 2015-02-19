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

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException
	{

		HttpServletResponse resp = (HttpServletResponse) response;

		// Add whatever headers you want here
		resp.setHeader("Cache-Control", "public, max-age=500000");
		resp.setHeader("Expires", DateTime.now().plusSeconds(500000) + "");

		chain.doFilter(request, response);
	}

	@Override
	public void destroy()
	{
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException
	{
	}

}

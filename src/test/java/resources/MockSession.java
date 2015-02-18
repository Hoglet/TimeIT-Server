package resources;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class MockSession implements HttpSession
{

	private final Map<String, Object>	attribute	= new HashMap<String, Object>();

	@Override
	public long getCreationTime()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getLastAccessedTime()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ServletContext getServletContext()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMaxInactiveInterval(int interval)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getMaxInactiveInterval()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public HttpSessionContext getSessionContext()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String name)
	{
		return attribute.get(name);
	}

	@Override
	public Object getValue(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getValueNames()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String name, Object value)
	{
		attribute.put(name, value);
	}

	@Override
	public void putValue(String name, Object value)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAttribute(String name)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeValue(String name)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void invalidate()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isNew()
	{
		// TODO Auto-generated method stub
		return false;
	}

}

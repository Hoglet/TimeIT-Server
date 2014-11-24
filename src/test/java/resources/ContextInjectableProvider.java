package resources;

import java.lang.reflect.Type;

import javax.ws.rs.core.Context;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

public class ContextInjectableProvider<T> extends SingletonTypeInjectableProvider<Context, T>
{
	public ContextInjectableProvider(Type type, T instance)
	{
		super(type, instance);
	}

}

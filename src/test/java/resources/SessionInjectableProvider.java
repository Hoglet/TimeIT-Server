package resources;

import io.dropwizard.jersey.sessions.Session;

import java.lang.reflect.Type;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

public class SessionInjectableProvider<T> extends SingletonTypeInjectableProvider<Session, T>
{
	public SessionInjectableProvider(Type type, T instance)
	{
		super(type, instance);
	}
}

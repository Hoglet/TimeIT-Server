package se.solit.timeit.entities;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.joda.time.DateTime;

import se.solit.timeit.serializers.DateAsTimestampSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class LoginKey
{

	private static final long	MILLISECONDS_PER_SECOND	= 1000;
	@Id
	@Column(nullable = false)
	private String				id;
	private User				user;

	@JsonSerialize(using = DateAsTimestampSerializer.class)
	long						lastChange;

	public LoginKey()
	{
	}

	public LoginKey(User user)
	{
		id = UUID.randomUUID().toString();
		this.user = user;
		lastChange = now();
	}

	private long now()
	{
		return DateTime.now().getMillis() / MILLISECONDS_PER_SECOND;
	}

	public UUID getId()
	{
		return UUID.fromString(id);
	}

	public User getUser()
	{
		return user;
	}

}

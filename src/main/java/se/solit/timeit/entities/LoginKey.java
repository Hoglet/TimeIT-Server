package se.solit.timeit.entities;

import java.time.Instant;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import se.solit.timeit.serializers.DateAsTimestampSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Immutable
public class LoginKey
{
	@Id
	@Column(nullable = false)
	private final String  id;
	private final User	  user;

	@JsonSerialize(using = DateAsTimestampSerializer.class)
	long                  lastChange;

	public LoginKey()
	{
		this(null);
	}

	public LoginKey(User user)
	{
		id = UUID.randomUUID().toString();
		this.user = user;
		lastChange = Instant.now().getEpochSecond();
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

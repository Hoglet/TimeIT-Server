package se.solit.timeit.serializers;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateAsTimestampDeserializer extends JsonDeserializer<DateTime>
{
	private static final int	MILLISECONDS_PER_SECOND	= 1000;

	@Override
	public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
	{
		return new DateTime(jp.getLongValue() * MILLISECONDS_PER_SECOND);
	}
}

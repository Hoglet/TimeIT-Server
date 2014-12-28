package se.solit.timeit.serializers;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateAsTimestampDeserializer extends JsonDeserializer<Date>
{
	private static final int	MILLISECONDS_PER_SECOND	= 1000;

	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
	{
		return new Date(jp.getLongValue() * MILLISECONDS_PER_SECOND);
	}
}

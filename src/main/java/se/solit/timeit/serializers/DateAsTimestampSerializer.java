package se.solit.timeit.serializers;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DateAsTimestampSerializer extends JsonSerializer<DateTime>
{
	private static final int	MILLISECONDS_PER_SECOND	= 1000;

	@Override
	public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException
	{
		jgen.writeNumber(value.getMillis() / MILLISECONDS_PER_SECOND);
	}
}

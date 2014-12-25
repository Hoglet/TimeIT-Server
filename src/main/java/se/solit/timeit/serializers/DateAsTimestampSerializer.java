package se.solit.timeit.serializers;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DateAsTimestampSerializer extends JsonSerializer<Date>
{
	private static final int	MILLISECONDS_PER_SECOND	= 1000;

	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException
	{
		jgen.writeString(String.valueOf(value.getTime() / MILLISECONDS_PER_SECOND));
	}
}

package se.solit.timeit.serializers;

import java.io.IOException;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DateAsTimestampSerializer extends JsonSerializer<ZonedDateTime>
{

	@Override
	public void serialize(ZonedDateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException
	{
		jgen.writeNumber(value.toInstant().getEpochSecond());
	}
}

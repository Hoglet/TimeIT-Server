package se.solit.timeit.serializers;

import java.io.IOException;
import java.time.Instant;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DateAsTimestampSerializer extends JsonSerializer<Instant>
{
	@Override
	public void serialize(Instant value, JsonGenerator jgen, SerializerProvider provider) throws IOException
	{
		jgen.writeNumber(value.getEpochSecond());
	}
}

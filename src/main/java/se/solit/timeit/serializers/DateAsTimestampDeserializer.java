package se.solit.timeit.serializers;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateAsTimestampDeserializer extends JsonDeserializer<Date>
{
	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		return new Date(Long.parseLong(jp.getText()) * 1000);
	}
}

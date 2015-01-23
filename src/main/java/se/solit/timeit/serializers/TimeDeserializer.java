package se.solit.timeit.serializers;

import java.io.IOException;
import java.util.UUID;

import org.joda.time.DateTime;

import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TimeDeserializer extends JsonDeserializer<Time>
{

	private static final long	MILLISECONDS_PER_SECOND	= 1000;

	@Override
	public Time deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		ObjectCodec oc = jp.getCodec();
		JsonNode node = oc.readTree(jp);
		ObjectMapper mapper = new ObjectMapper();

		UUID id = UUID.fromString(node.get("id").textValue());
		long millis = node.get("start").longValue() * MILLISECONDS_PER_SECOND;
		DateTime start = new DateTime(millis);
		millis = node.get("stop").longValue() * MILLISECONDS_PER_SECOND;
		DateTime stop = new DateTime(millis);

		boolean deleted = node.get("deleted").asBoolean();
		millis = node.get("changed").longValue() * MILLISECONDS_PER_SECOND;
		DateTime lastChanged = new DateTime(millis);

		JsonNode jn = node.get("task");
		Task task = mapper.readValue(jn.traverse(oc), Task.class);
		return new Time(id, start, stop, deleted, lastChanged, task);
	}
}

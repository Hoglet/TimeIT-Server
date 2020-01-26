package se.solit.timeit.serializers;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

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

	@Override
	public Time deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		ObjectCodec oc = jp.getCodec();
		JsonNode node = oc.readTree(jp);
		ObjectMapper mapper = new ObjectMapper();
		ZoneId zone = ZonedDateTime.now().getZone();
		
		UUID id = UUID.fromString(node.get("id").textValue());
		
		Instant startInstant = Instant.ofEpochSecond(node.get("start").longValue());
		ZonedDateTime start = startInstant.atZone(zone);
		
		Instant stopInstant = Instant.ofEpochSecond(node.get("stop").longValue());
		ZonedDateTime stop = stopInstant.atZone(zone);

		boolean deleted = node.get("deleted").asBoolean();
		
		Instant changedInstant = Instant.ofEpochSecond(node.get("changed").longValue());
		ZonedDateTime lastChanged = changedInstant.atZone(zone);

		JsonNode jn = node.get("task");
		Task task = mapper.readValue(jn.traverse(oc), Task.class);
		return new Time(id, start, stop, deleted, lastChanged, task);
	}
}

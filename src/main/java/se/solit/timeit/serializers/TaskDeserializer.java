package se.solit.timeit.serializers;

import java.io.IOException;
import java.util.UUID;

import org.joda.time.DateTime;

import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TaskDeserializer extends JsonDeserializer<Task>
{

	private static final long	MILLISECONDS_PER_SECOND	= 1000;

	@Override
	public Task deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		ObjectCodec oc = jp.getCodec();
		JsonNode node = oc.readTree(jp);
		ObjectMapper mapper = new ObjectMapper();

		UUID id = UUID.fromString(node.get("id").textValue());
		String name = "";
		boolean completed = false;
		DateTime lastChanged = DateTime.now();
		boolean deleted = false;
		User owner = new User();
		Task parent = null;
		if (node.findValue("lastChange") != null)
		{
			name = node.get("name").textValue();

			long millis = node.get("lastChange").longValue() * MILLISECONDS_PER_SECOND;
			lastChanged = new DateTime(millis);

			deleted = node.get("deleted").asBoolean();
			JsonNode jn = node.get("owner");
			owner = mapper.readValue(jn.traverse(oc), User.class);
			jn = node.get("parent");
			if (jn != null)
			{
				parent = mapper.readValue(jn.traverse(oc), Task.class);
			}
			completed = node.get("completed").asBoolean();
		}
		return new Task(id, name, parent, completed, lastChanged, deleted, owner);
	}
}

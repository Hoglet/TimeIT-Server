package se.solit.timeit.serializers;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

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

	@Override
	public Task deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		ObjectCodec oc = jp.getCodec();
		JsonNode node = oc.readTree(jp);
		ObjectMapper mapper = new ObjectMapper();

		UUID id = UUID.fromString(node.get("id").textValue());
		String name = "";
		boolean completed = false;
		Instant lastChanged = Instant.now();
		boolean deleted = false;
		User owner = new User();
		Task parent = null;
		if (node.findValue("lastChange") != null)
		{
			name = node.get("name").textValue();

			lastChanged = Instant.ofEpochSecond(node.get("lastChange").longValue());

			deleted = node.get("deleted").asBoolean();
			JsonNode jn = node.get("owner");
			owner = mapper.readValue(jn.traverse(oc), User.class);
			jn = node.get("parent");
			if (jn != null)
			{
				parent = mapper.readValue(jn.traverse(oc), Task.class);
			}
		}
		return new Task(id, name, parent, lastChanged, deleted, owner);
	}
}

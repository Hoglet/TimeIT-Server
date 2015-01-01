package se.solit.timeit.serializers;

import java.io.IOException;

import se.solit.timeit.entities.Task;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TaskSerializer extends JsonSerializer<Task>
{

	@Override
	public void serialize(Task value, JsonGenerator jgen, SerializerProvider provider) throws IOException
	{
		jgen.writeStartObject();
		jgen.writeStringField("id", value.getID().toString());
		jgen.writeEndObject();

	}
}

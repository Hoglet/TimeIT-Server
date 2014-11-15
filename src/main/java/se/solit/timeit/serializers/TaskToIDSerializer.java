package se.solit.timeit.serializers;

import java.io.IOException;

import se.solit.timeit.entities.Task;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TaskToIDSerializer extends JsonSerializer<Task>
{

	@Override
	public void serialize(Task tmpTask, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException, JsonProcessingException
	{
		jsonGenerator.writeObject(tmpTask.getID());
	}

}

package se.solit.timeit.serializers;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.entities.Task;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class TaskDeSerializer extends JsonDeserializer<Task>
{
	private final EntityManagerFactory	emf;

	public TaskDeSerializer(EntityManagerFactory emf)
	{
		this.emf = emf;
	}

	@Override
	public Task deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		TaskDAO taskDAO = new TaskDAO(emf);
		JsonNode node = jp.getCodec().readTree(jp);

		String taskID = node.textValue();
		return taskDAO.getByID(taskID);
	}

}

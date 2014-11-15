package se.solit.timeit.serializers;

import java.io.IOException;

import se.solit.timeit.entities.User;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class UserToIDSerializer extends JsonSerializer<User>
{

	@Override
	public void serialize(User tmpUser, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException, JsonProcessingException
	{
		jsonGenerator.writeObject(tmpUser.getUsername());
	}

}

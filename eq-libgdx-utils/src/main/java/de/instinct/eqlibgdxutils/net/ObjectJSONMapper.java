package de.instinct.eqlibgdxutils.net;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectJSONMapper {
	
	private static ObjectMapper objectMapper;
	
	public static String mapObject(Object object) {
		init();
		try {
			return objectMapper.writer().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T mapJSON(String json, Class<T> type) {
		init();
		try {
			return objectMapper.readValue(json, type);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static void init() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
	}

}

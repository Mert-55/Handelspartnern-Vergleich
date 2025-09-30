package org.iu.handelspartnern.spark.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.Map;

public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private JsonUtils() {
    }

    public static ObjectMapper mapper() {
        return OBJECT_MAPPER;
    }

    public static <T> T readValue(String json, Class<T> type) throws IOException {
        return OBJECT_MAPPER.readValue(json, type);
    }

    public static <T> T readValue(String json, TypeReference<T> typeReference) throws IOException {
        return OBJECT_MAPPER.readValue(json, typeReference);
    }

    public static JsonNode readTree(String json) throws JsonProcessingException {
        return OBJECT_MAPPER.readTree(json);
    }

    public static Map<String, Object> readToMap(String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
        });
    }

    public static String write(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize response to JSON", e);
        }
    }
}

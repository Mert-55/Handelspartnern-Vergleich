package org.iu.handelspartnern.common.entity.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.iu.handelspartnern.common.entity.Contact;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter
public class ContactConverter implements AttributeConverter<List<Contact>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Contact> contacts) {
        if (contacts == null || contacts.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(contacts);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting contacts to JSON", e);
        }
    }

    @Override
    public List<Contact> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Contact>>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to contacts", e);
        }
    }
}

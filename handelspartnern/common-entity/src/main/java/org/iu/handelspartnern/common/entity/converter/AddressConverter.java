package org.iu.handelspartnern.common.entity.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.iu.handelspartnern.common.entity.Address;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter
public class AddressConverter implements AttributeConverter<List<Address>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(addresses);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting addresses to JSON", e);
        }
    }

    @Override
    public List<Address> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Address>>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to addresses", e);
        }
    }
}

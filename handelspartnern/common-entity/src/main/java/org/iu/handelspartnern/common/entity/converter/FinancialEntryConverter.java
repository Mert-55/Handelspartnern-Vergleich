package org.iu.handelspartnern.common.entity.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.iu.handelspartnern.common.entity.FinancialEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter
public class FinancialEntryConverter implements AttributeConverter<List<FinancialEntry>, String> {

    private static final Logger log = LoggerFactory.getLogger(FinancialEntryConverter.class);

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Override
    public String convertToDatabaseColumn(List<FinancialEntry> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("[FinancialEntryConverter] Error serializing entries: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Error serializing financial entries", e);
        }
    }

    @Override
    public List<FinancialEntry> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<FinancialEntry>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("[FinancialEntryConverter] Error deserializing entries: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}

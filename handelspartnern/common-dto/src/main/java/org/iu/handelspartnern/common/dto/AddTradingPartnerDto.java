package org.iu.handelspartnern.common.dto;

import org.iu.handelspartnern.common.entity.PartnerType;
import java.util.Optional;

public record AddTradingPartnerDto(String name, Optional<String> about, Optional<String> taxId,
        Optional<String> paymentTerms, Optional<String> corporateImageUrl, PartnerType partnerType) {
    public AddTradingPartnerDto {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (partnerType == null) {
            throw new IllegalArgumentException("Partner type cannot be null");
        }
    }
}

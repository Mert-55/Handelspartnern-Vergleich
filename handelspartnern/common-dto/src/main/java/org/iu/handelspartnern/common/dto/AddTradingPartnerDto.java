package org.iu.handelspartnern.common.dto;

import org.iu.handelspartnern.common.entity.PartnerType;
import java.util.Optional;

public record AddTradingPartnerDto(String name, Optional<String> about, Optional<String> taxId,
        Optional<String> paymentTerms, Optional<String> corporateImageUrl, PartnerType type) {

    public AddTradingPartnerDto {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Partner type cannot be null");
        }
    }

    public PartnerType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getAbout() {
        return about;
    }

    public Optional<String> getTaxId() {
        return taxId;
    }

    public Optional<String> getPaymentTerms() {
        return paymentTerms;
    }

    public Optional<String> getCorporateImageUrl() {
        return corporateImageUrl;
    }
}

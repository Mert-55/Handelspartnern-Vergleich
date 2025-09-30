package org.iu.handelspartnern.common.dto;

import org.iu.handelspartnern.common.entity.PartnerStatus;
import org.iu.handelspartnern.common.entity.PartnerType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradingPartnerListDto(Long id, String name, String corporateImageUrl, PartnerType type,
                PartnerStatus status, BigDecimal claims, BigDecimal payable, LocalDateTime dateModified) {

        public Long getId() {
                return id;
        }

        public String getName() {
                return name;
        }

        public String getCorporateImageUrl() {
                return corporateImageUrl;
        }

        public PartnerType getType() {
                return type;
        }

        public PartnerStatus getStatus() {
                return status;
        }

        public BigDecimal getClaims() {
                return claims;
        }

        public BigDecimal getPayable() {
                return payable;
        }

        public LocalDateTime getDateModified() {
                return dateModified;
        }

        public LocalDateTime getUpdated() {
                return dateModified;
        }
}
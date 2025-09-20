package org.iu.handelspartnern.common.dto;

import org.iu.handelspartnern.common.entity.PartnerStatus;
import org.iu.handelspartnern.common.entity.PartnerType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradingPartnerListDto(Long id, String name, String corporateImageUrl, PartnerType type,
        PartnerStatus status, BigDecimal claims, BigDecimal payable, LocalDateTime dateModified) {
}
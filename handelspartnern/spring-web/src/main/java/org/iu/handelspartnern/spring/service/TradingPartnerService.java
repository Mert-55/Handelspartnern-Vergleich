package org.iu.handelspartnern.spring.service;

import org.iu.handelspartnern.common.entity.TradingPartner;
import org.iu.handelspartnern.common.entity.PartnerStatus;
import org.iu.handelspartnern.common.entity.PartnerType;
import org.iu.handelspartnern.common.dto.AddTradingPartnerDto;
import org.iu.handelspartnern.common.dto.TradingPartnerListDto;
import org.iu.handelspartnern.spring.repository.TradingPartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TradingPartnerService {

    @Autowired
    private TradingPartnerRepository repository;

    @Transactional(readOnly = true)
    public List<TradingPartnerListDto> getAllPartners(PartnerType type, PartnerStatus status, String search) {
        return repository.findWithFilters(type, status, search).stream().map(this::convertToListDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TradingPartner> getPartnerById(Long id) {
        return repository.findById(id);
    }

    public TradingPartner createPartner(AddTradingPartnerDto dto) {
        TradingPartner partner = new TradingPartner();
        partner.setName(dto.name());
        partner.setAbout(dto.about().orElse(""));
        partner.setTaxId(dto.taxId().orElse("DE000000000"));
        partner.setPaymentTerms(dto.paymentTerms().orElse("Net 30"));
        partner.setCorporateImageUrl(dto.corporateImageUrl().orElse(null));
        partner.setType(dto.partnerType());
        partner.setStatus(PartnerStatus.PENDING_APPROVAL);

        return repository.save(partner);
    }

    public TradingPartner updatePartner(Long id, TradingPartner updatedPartner) {
        return repository.findById(id).map(existing -> {
            existing.setName(updatedPartner.getName());
            existing.setAbout(updatedPartner.getAbout());
            existing.setTaxId(updatedPartner.getTaxId());
            existing.setPaymentTerms(updatedPartner.getPaymentTerms());
            existing.setCorporateImageUrl(updatedPartner.getCorporateImageUrl());
            existing.setType(updatedPartner.getType());
            existing.setStatus(updatedPartner.getStatus());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Partner not found: " + id));
    }

    public void deletePartner(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TradingPartnerListDto> getDashboardPartners() {
        return repository.findByStatusOrderByDateModifiedDesc(PartnerStatus.ACTIVE).stream().limit(10)
                .map(this::convertToListDto).collect(Collectors.toList());
    }

    private TradingPartnerListDto convertToListDto(TradingPartner partner) {
        return new TradingPartnerListDto(partner.getId(), partner.getName(), partner.getCorporateImageUrl(),
                partner.getType(), partner.getStatus(), partner.getClaims(), partner.getPayable(),
                partner.getDateModified());
    }
}

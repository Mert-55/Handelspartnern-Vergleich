package org.iu.handelspartnern.spring.service;

import org.iu.handelspartnern.common.entity.PartnerStatus;
import org.iu.handelspartnern.common.entity.PartnerType;
import org.iu.handelspartnern.common.entity.TradingPartner;
import org.iu.handelspartnern.common.entity.FinancialEntry;
import org.iu.handelspartnern.common.entity.FinancialEntryStatus;
import org.iu.handelspartnern.common.dto.AddTradingPartnerDto;
import org.iu.handelspartnern.common.dto.TradingPartnerListDto;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.iu.handelspartnern.spring.repository.TradingPartnerRepository;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TradingPartnerService {

    private final TradingPartnerRepository repository;

    public TradingPartnerService(TradingPartnerRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TradingPartnerListDto> getAllPartners() {
        return getAllPartners(null, null, null);
    }

    @Transactional(readOnly = true)
    public List<TradingPartnerListDto> getAllPartners(PartnerType type, PartnerStatus status, String search) {
        String searchLower = (search != null && !search.trim().isEmpty()) ? search.trim().toLowerCase() : null;

        return repository.findAll(Sort.by(Sort.Direction.DESC, "dateModified")).stream()
                .filter(partner -> type == null || type.equals(partner.getType()))
                .filter(partner -> status == null || status.equals(partner.getStatus()))
                .filter(partner -> {
                    if (searchLower == null) {
                        return true;
                    }
                    String nameMatch = partner.getName() != null ? partner.getName().toLowerCase() : "";
                    String taxIdMatch = partner.getTaxId() != null ? partner.getTaxId().toLowerCase() : "";
                    String aboutMatch = partner.getAbout() != null ? partner.getAbout().toLowerCase() : "";
                    return nameMatch.contains(searchLower) || taxIdMatch.contains(searchLower)
                            || aboutMatch.contains(searchLower);
                })
                .map(this::convertToListDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TradingPartner> getPartnerById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public TradingPartner createPartner(AddTradingPartnerDto dto) {
        try {
            System.out.println("createPartner called with: " + dto.getName());

            TradingPartner partner = new TradingPartner();
            partner.setName(dto.getName());
            partner.setAbout(dto.getAbout().orElse(null));
            partner.setTaxId(dto.getTaxId().orElse("DE000000000"));
            partner.setPaymentTerms(dto.getPaymentTerms().orElse("30 Tage"));
            partner.setCorporateImageUrl(dto.getCorporateImageUrl().orElse(null));
            partner.setType(dto.getType());
            partner.setStatus(PartnerStatus.ACTIVE);
            partner.setClaims(BigDecimal.ZERO);
            partner.setPayable(BigDecimal.ZERO);

            TradingPartner saved = repository.save(partner);
            System.out.println("Partner created with id: " + saved.getId());
            return saved;

        } catch (Exception e) {
            System.err.println("Error creating partner: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Erstellen des Partners: " + e.getMessage());
        }
    }

    @Transactional
    public TradingPartner updatePartner(Long id, TradingPartner updatedPartner) {
        try {
            System.out.println("updatePartner called for id: " + id);

            TradingPartner existingPartner = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Partner mit ID " + id + " nicht gefunden"));

            existingPartner.setName(updatedPartner.getName());
            existingPartner.setType(updatedPartner.getType());
            existingPartner.setStatus(updatedPartner.getStatus());
            existingPartner.setTaxId(updatedPartner.getTaxId());
            existingPartner.setPaymentTerms(updatedPartner.getPaymentTerms());
            existingPartner.setAbout(updatedPartner.getAbout());
            existingPartner.setCorporateImageUrl(updatedPartner.getCorporateImageUrl());

            existingPartner.setContacts(new ArrayList<>(updatedPartner.getContacts()));
            existingPartner.setAddresses(new ArrayList<>(updatedPartner.getAddresses()));

            if (updatedPartner.getFinancialEntries() != null) {
                existingPartner.setFinancialEntries(new ArrayList<>(updatedPartner.getFinancialEntries()));
            } else {
                existingPartner.recalculateFinancials();
            }

            if (updatedPartner.getClaims() != null) {
                existingPartner.setClaims(updatedPartner.getClaims());
            }
            if (updatedPartner.getPayable() != null) {
                existingPartner.setPayable(updatedPartner.getPayable());
            }

            existingPartner.setUpdated(LocalDateTime.now());

            TradingPartner saved = repository.save(existingPartner);
            System.out.println("Partner updated: " + saved.getName());
            return saved;

        } catch (Exception e) {
            System.err.println("Error updating partner: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Aktualisieren des Partners: " + e.getMessage());
        }
    }

    @Transactional
    public void deletePartner(Long id) {
        try {
            System.out.println("deletePartner called for id: " + id);

            if (!repository.existsById(id)) {
                throw new RuntimeException("Partner mit ID " + id + " nicht gefunden");
            }

            repository.deleteById(id);
            System.out.println("Partner deleted: " + id);

        } catch (Exception e) {
            System.err.println("Error deleting partner: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Löschen des Partners: " + e.getMessage());
        }
    }

    // ===== HELPER METHODS =====

    private TradingPartnerListDto convertToListDto(TradingPartner partner) {
        try {
            return new TradingPartnerListDto(
                    partner.getId(),
                    partner.getName(),
                    partner.getCorporateImageUrl(),
                    partner.getType(),
                    partner.getStatus(),
                    partner.getClaims(),
                    partner.getPayable(),
                    partner.getUpdated());

        } catch (Exception e) {
            System.err.println("Error converting to DTO: " + e.getMessage());
            return new TradingPartnerListDto(
                    partner.getId(),
                    partner.getName() != null ? partner.getName() : "Unknown",
                    null,
                    partner.getType(),
                    partner.getStatus(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    LocalDateTime.now());
        }
    }

    // ===== FINANZ-METHODEN =====

    @Transactional
    public TradingPartner addFinancialEntry(Long partnerId, FinancialEntry entry) {
        TradingPartner partner = repository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner mit ID " + partnerId + " nicht gefunden"));

        if (entry.getDate() == null) {
            entry.setDate(LocalDate.now());
        }
        if (entry.getId() == null) {
            entry.setId(UUID.randomUUID());
        }
        if (entry.getStatus() == null) {
            entry.setStatus(FinancialEntryStatus.OPEN);
        }
        partner.addFinancialEntry(entry);
        partner.setUpdated(LocalDateTime.now());

        return repository.save(partner);
    }

    @Transactional
    public TradingPartner updateFinancialEntryStatus(Long partnerId, UUID entryId, FinancialEntryStatus status) {
        TradingPartner partner = repository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner mit ID " + partnerId + " nicht gefunden"));

        boolean updated = false;
        for (FinancialEntry entry : partner.getFinancialEntries()) {
            if (entry != null && entryId.equals(entry.getId())) {
                entry.setStatus(status);
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new RuntimeException("Finanztransaktion mit ID " + entryId + " nicht gefunden");
        }

        partner.recalculateFinancials();
        partner.setUpdated(LocalDateTime.now());
        return repository.save(partner);
    }

    @Transactional
    public void addClaim(Long partnerId, BigDecimal amount) {
        TradingPartner partner = repository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner mit ID " + partnerId + " nicht gefunden"));

        BigDecimal currentClaims = partner.getClaims() != null ? partner.getClaims() : BigDecimal.ZERO;
        partner.setClaims(currentClaims.add(amount));
        partner.setUpdated(LocalDateTime.now());
        repository.save(partner);
    }

    @Transactional
    public void addPayable(Long partnerId, BigDecimal amount) {
        TradingPartner partner = repository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner mit ID " + partnerId + " nicht gefunden"));

        BigDecimal currentPayables = partner.getPayable() != null ? partner.getPayable() : BigDecimal.ZERO;
        partner.setPayable(currentPayables.add(amount));
        partner.setUpdated(LocalDateTime.now());
        repository.save(partner);
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getPartnerBalance(Long partnerId) {
        TradingPartner partner = repository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner mit ID " + partnerId + " nicht gefunden"));

        TradingPartner.FinancialOverview overview = partner.getFinancialOverview();

        Map<String, BigDecimal> balances = new HashMap<>();
        balances.put("openClaims", overview.getOpenClaims());
        balances.put("settledClaims", overview.getSettledClaims());
        balances.put("openPayables", overview.getOpenPayables());
        balances.put("settledPayables", overview.getSettledPayables());
        balances.put("claims", partner.getClaims());
        balances.put("payable", partner.getPayable());
        return balances;
    }
}

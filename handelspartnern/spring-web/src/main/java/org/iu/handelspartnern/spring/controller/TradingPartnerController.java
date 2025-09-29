package org.iu.handelspartnern.spring.controller;

import org.iu.handelspartnern.common.entity.PartnerStatus;
import org.iu.handelspartnern.common.entity.PartnerType;
import org.iu.handelspartnern.common.entity.TradingPartner;
import org.iu.handelspartnern.common.entity.Contact;
import org.iu.handelspartnern.common.entity.Address;
import org.iu.handelspartnern.common.dto.AddTradingPartnerDto;
import org.iu.handelspartnern.common.dto.TradingPartnerListDto;
import org.iu.handelspartnern.spring.service.TradingPartnerService;
import org.iu.handelspartnern.common.entity.FinancialEntry;
import org.iu.handelspartnern.common.entity.FinancialEntryStatus;
import org.iu.handelspartnern.common.entity.FinancialEntryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Map;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class TradingPartnerController {

    @Autowired
    private TradingPartnerService tradingPartnerService;

    // Main Page - Full HTML
    @GetMapping("/")
    public String index(Model model,
            @RequestParam(required = false) PartnerType type,
            @RequestParam(required = false) PartnerStatus status,
            @RequestParam(required = false) String search) {

        try {
            List<TradingPartnerListDto> partners = tradingPartnerService.getAllPartners(type, status, search);
            model.addAttribute("partners", partners);
            model.addAttribute("partnerTypes", PartnerType.values());
            model.addAttribute("partnerStatuses", PartnerStatus.values());

            return "index";
        } catch (Exception e) {
            System.err.println("Error in index: " + e.getMessage());
            e.printStackTrace();

            // Fallback mit leerer Liste
            model.addAttribute("partners", new ArrayList<TradingPartnerListDto>());
            model.addAttribute("partnerTypes", PartnerType.values());
            model.addAttribute("partnerStatuses", PartnerStatus.values());

            return "index";
        }
    }

    // Partner List Fragment - KORRIGIERT
    @GetMapping("/partners")
    public String partnersList(Model model,
            @RequestParam(required = false) PartnerType type,
            @RequestParam(required = false) PartnerStatus status,
            @RequestParam(required = false) String search,
            HttpServletRequest request) {

        try {
            System.out.println("GET /partners called - type: " + type + ", status: " + status + ", search: " + search);

            List<TradingPartnerListDto> partners = tradingPartnerService.getAllPartners(type, status, search);
            model.addAttribute("partners", partners);

            System.out.println("Found " + partners.size() + " partners");

            // Return fragment for HTMX requests
            if (isHtmxRequest(request)) {
                return "fragments/partner-list :: partner-list";
            }

            return "redirect:/";

        } catch (Exception e) {
            System.err.println("Error in partnersList: " + e.getMessage());
            e.printStackTrace();

            // Fallback
            model.addAttribute("partners", new ArrayList<TradingPartnerListDto>());

            if (isHtmxRequest(request)) {
                return "fragments/partner-list :: partner-list";
            }

            return "redirect:/";
        }
    }

    // Partner Detail Fragment - KORRIGIERT
    @GetMapping("/partners/{id}")
    public String partnerDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        try {
            System.out.println("GET /partners/" + id + " called");

            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(id);
            if (partnerOpt.isPresent()) {
                model.addAttribute("partner", partnerOpt.get());

                if (isHtmxRequest(request)) {
                    return "fragments/partner-detail :: partner-detail";
                }
            } else {
                System.err.println("Partner with id " + id + " not found");
            }
        } catch (Exception e) {
            System.err.println("Error in partnerDetail: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/";
    }

    // New Partner Form Fragment - KORRIGIERT
    @GetMapping("/partners/new")
    public String newPartnerForm(Model model, HttpServletRequest request) {
        try {
            System.out.println("GET /partners/new called");

            TradingPartner emptyPartner = new TradingPartner();
            emptyPartner.setClaims(BigDecimal.ZERO);
            emptyPartner.setPayable(BigDecimal.ZERO);
            emptyPartner.setPaymentTerms("30 Tage");

            model.addAttribute("partner", emptyPartner);
            model.addAttribute("partnerTypes", PartnerType.values());
            model.addAttribute("partnerStatuses", PartnerStatus.values());
            model.addAttribute("isNewPartner", true);

            if (isHtmxRequest(request)) {
                return "fragments/partner-form :: partner-form";
            }

            return "redirect:/";

        } catch (Exception e) {
            System.err.println("Error in newPartnerForm: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/";
        }
    }

    // Edit Partner Form Fragment - KORRIGIERT
    @GetMapping("/partners/{id}/edit")
    public String editPartnerForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        try {
            System.out.println("GET /partners/" + id + "/edit called");

            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(id);
            if (partnerOpt.isPresent()) {
                model.addAttribute("partner", partnerOpt.get());
                model.addAttribute("partnerTypes", PartnerType.values());
                model.addAttribute("partnerStatuses", PartnerStatus.values());
                model.addAttribute("isNewPartner", false);

                if (isHtmxRequest(request)) {
                    return "fragments/partner-form :: partner-form";
                }
            } else {
                System.err.println("Partner with id " + id + " not found for editing");
            }
        } catch (Exception e) {
            System.err.println("Error in editPartnerForm: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/";
    }

    // Create Partner - KORRIGIERT
    @PostMapping("/partners")
    public String createPartner(@RequestParam("name") String name,
            @RequestParam("type") PartnerType type,
            @RequestParam("status") PartnerStatus status,
            @RequestParam("taxId") String taxId,
            @RequestParam("paymentTerms") String paymentTerms,
            @RequestParam(value = "claims", defaultValue = "0") BigDecimal claims,
            @RequestParam(value = "payable", defaultValue = "0") BigDecimal payable,
            @RequestParam(value = "contacts", required = false) String contacts,
            @RequestParam(value = "addresses", required = false) String addresses,
            @RequestParam(value = "about", required = false) String about,
            @RequestParam(value = "corporateImageUrl", required = false) String corporateImageUrl,
            Model model,
            HttpServletRequest request) {

        System.out.println("POST /partners called: " + name);

        try {
            // Validation
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name ist erforderlich");
            }

            if (taxId == null || taxId.trim().isEmpty()) {
                throw new IllegalArgumentException("Steuernummer ist erforderlich");
            }

            // Create DTO
            AddTradingPartnerDto dto = new AddTradingPartnerDto(
                    name,
                    Optional.ofNullable(about),
                    Optional.ofNullable(taxId),
                    Optional.ofNullable(paymentTerms),
                    Optional.ofNullable(corporateImageUrl),
                    type);

            TradingPartner partner = tradingPartnerService.createPartner(dto);
            partner.setStatus(status);
            partner.setClaims(claims);
            partner.setPayable(payable);
            if (contacts != null) {
                partner.setContactsAsString(contacts);
            }
            if (addresses != null) {
                partner.setAddressesAsString(addresses);
            }

            tradingPartnerService.updatePartner(partner.getId(), partner);

            System.out.println("Partner created successfully");

            // Return updated partner list
            List<TradingPartnerListDto> partners = tradingPartnerService.getAllPartners();
            model.addAttribute("partners", partners);

            if (isHtmxRequest(request)) {
                return "fragments/partner-list :: partner-list";
            }

        } catch (Exception e) {
            System.err.println("Error creating partner: " + e.getMessage());
            e.printStackTrace();

            // Return to form with error
            TradingPartner partnerForm = new TradingPartner();
            partnerForm.setName(name);
            partnerForm.setType(type);
            partnerForm.setStatus(status);
            partnerForm.setTaxId(taxId);
            partnerForm.setPaymentTerms(paymentTerms);
            partnerForm.setClaims(claims);
            partnerForm.setPayable(payable);
            partnerForm.setAbout(about);
            partnerForm.setCorporateImageUrl(corporateImageUrl);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("partner", partnerForm);
            model.addAttribute("partnerTypes", PartnerType.values());
            model.addAttribute("partnerStatuses", PartnerStatus.values());
            model.addAttribute("isNewPartner", true);

            if (isHtmxRequest(request)) {
                return "fragments/partner-form :: partner-form";
            }
        }

        return "redirect:/";
    }

    // Update Partner - KORRIGIERT
    @PutMapping("/partners/{id}")
    public String updatePartner(@PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("type") PartnerType type,
            @RequestParam("status") PartnerStatus status,
            @RequestParam("taxId") String taxId,
            @RequestParam("paymentTerms") String paymentTerms,
            @RequestParam(value = "claims", defaultValue = "0") BigDecimal claims,
            @RequestParam(value = "payable", defaultValue = "0") BigDecimal payable,
            @RequestParam(value = "contacts", required = false) String contacts,
            @RequestParam(value = "addresses", required = false) String addresses,
            @RequestParam(value = "about", required = false) String about,
            @RequestParam(value = "corporateImageUrl", required = false) String corporateImageUrl,
            Model model,
            HttpServletRequest request) {

        System.out.println("PUT /partners/" + id + " called");

        try {
            Optional<TradingPartner> existingOpt = tradingPartnerService.getPartnerById(id);
            if (existingOpt.isPresent()) {
                TradingPartner partner = existingOpt.get();
                partner.setName(name);
                partner.setType(type);
                partner.setStatus(status);
                partner.setTaxId(taxId);
                partner.setPaymentTerms(paymentTerms);
                partner.setClaims(claims);
                partner.setPayable(payable);
                if (contacts != null) {
                    partner.setContactsAsString(contacts);
                }
                if (addresses != null) {
                    partner.setAddressesAsString(addresses);
                }
                partner.setAbout(about);
                partner.setCorporateImageUrl(corporateImageUrl);

                tradingPartnerService.updatePartner(id, partner);

                // Return updated partner list
                List<TradingPartnerListDto> partners = tradingPartnerService.getAllPartners();
                model.addAttribute("partners", partners);

                if (isHtmxRequest(request)) {
                    return "fragments/partner-list :: partner-list";
                }
            }

        } catch (Exception e) {
            System.err.println("Error updating partner: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/";
    }

    // Delete Partner - KORRIGIERT
    @DeleteMapping("/partners/{id}")
    public String deletePartner(@PathVariable Long id, Model model, HttpServletRequest request) {
        try {
            System.out.println("DELETE /partners/" + id + " called");

            tradingPartnerService.deletePartner(id);

            List<TradingPartnerListDto> partners = tradingPartnerService.getAllPartners();
            model.addAttribute("partners", partners);

            if (isHtmxRequest(request)) {
                return "fragments/partner-list :: partner-list";
            }

        } catch (Exception e) {
            System.err.println("Error deleting partner: " + e.getMessage());
            e.printStackTrace();

            List<TradingPartnerListDto> partners = tradingPartnerService.getAllPartners();
            model.addAttribute("partners", partners);

            if (isHtmxRequest(request)) {
                return "fragments/partner-list :: partner-list";
            }
        }

        return "redirect:/";
    }

    // Utility method to check HTMX requests
    private boolean isHtmxRequest(HttpServletRequest request) {
        return "true".equals(request.getHeader("HX-Request"));
    }
}

// SEPARATE API-CONTROLLER FOR REST ENDPOINTS
@RestController
@RequestMapping("/api")
class ApiController {

    @Autowired
    private TradingPartnerService tradingPartnerService;

    // Financial Balance
    @GetMapping("/partners/{id}/balance")
    public ResponseEntity<?> getPartnerBalance(@PathVariable Long id) {
        try {
            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(id);
            if (partnerOpt.isPresent()) {
                TradingPartner partner = partnerOpt.get();
                Map<String, Object> balance = new HashMap<>();
                balance.put("claims", partner.getClaims() != null ? partner.getClaims() : BigDecimal.ZERO);
                balance.put("payables", partner.getPayable() != null ? partner.getPayable() : BigDecimal.ZERO);
                balance.put("partnerId", id);
                return ResponseEntity.ok(balance);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/partners/financial-transaction")
    public ResponseEntity<?> addFinancialTransaction(@RequestBody Map<String, Object> transactionData) {
        try {
            Object partnerIdRaw = transactionData.get("partnerId");
            if (partnerIdRaw == null) {
                return badRequest("Partner ID fehlt.");
            }

            Long partnerId = Long.valueOf(partnerIdRaw.toString());

            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(partnerId);
            if (partnerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String typeRaw = Objects.toString(transactionData.get("type"), "").trim();
            if (typeRaw.isEmpty()) {
                return badRequest("Transaktionstyp ist erforderlich.");
            }

            FinancialEntryType entryType = FinancialEntryType.valueOf(typeRaw.toUpperCase(Locale.ROOT));

            BigDecimal amount = new BigDecimal(Objects.toString(transactionData.get("amount"), "0"));
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return badRequest("Betrag muss größer als 0 sein.");
            }

            FinancialEntry entry = new FinancialEntry(
                    entryType,
                    FinancialEntryStatus.OPEN,
                    amount,
                    Objects.toString(transactionData.get("purpose"), ""),
                    Objects.toString(transactionData.get("reference"), ""),
                    Optional.ofNullable(transactionData.get("date"))
                            .map(Object::toString)
                            .filter(value -> !value.isBlank())
                            .map(LocalDate::parse)
                            .orElse(LocalDate.now()));

            TradingPartner updated = tradingPartnerService.addFinancialEntry(partnerId, entry);
            return okWithFinancials(updated, "Transaktion erfolgreich erfasst.");

        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            byte[] bytes = ("Fehler beim Export: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<>(bytes, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/partners/{id}/transactions")
    public ResponseEntity<?> getPartnerTransactions(@PathVariable Long id) {
        try {
            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(id);
            if (partnerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            TradingPartner partner = partnerOpt.get();
            TradingPartner.FinancialOverview overview = partner.getFinancialOverview();

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> overviewMap = new HashMap<>();

            BigDecimal openClaims = overview.getOpenClaims();
            BigDecimal settledClaims = overview.getSettledClaims();
            BigDecimal openPayables = overview.getOpenPayables();
            BigDecimal settledPayables = overview.getSettledPayables();

            BigDecimal totalClaims = openClaims.add(settledClaims);
            BigDecimal totalPayables = openPayables.add(settledPayables);
            BigDecimal netBalance = totalClaims.subtract(totalPayables);

            overviewMap.put("openClaims", openClaims);
            overviewMap.put("settledClaims", settledClaims);
            overviewMap.put("openPayables", openPayables);
            overviewMap.put("settledPayables", settledPayables);
            overviewMap.put("totalClaims", totalClaims);
            overviewMap.put("totalPayables", totalPayables);
            overviewMap.put("netBalance", netBalance);
            overviewMap.put("transactionCount", overview.getTransactionCount());

            response.put("overview", overviewMap);
            response.put("partner", Map.of(
                    "id", partner.getId(),
                    "name", partner.getName(),
                    "taxId", partner.getTaxId(),
                    "status", partner.getStatus(),
                    "type", partner.getType()));

            List<Map<String, Object>> transactions = partner.getFinancialEntries().stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator
                            .comparing((FinancialEntry entry) -> entry.getDate(),
                                    Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(FinancialEntry::getCreatedAt,
                                    Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed())
                    .map(entry -> {
                        Map<String, Object> tx = new HashMap<>();
                        tx.put("id", entry.getId());
                        tx.put("type", entry.getType());
                        tx.put("typeLabel",
                                entry.getType() == FinancialEntryType.CLAIM ? "Forderung" : "Verbindlichkeit");
                        tx.put("status", entry.getStatus());
                        tx.put("statusLabel", entry.getStatusLabel());
                        tx.put("amount", entry.getAmount());
                        tx.put("purpose", entry.getPurpose());
                        tx.put("reference", entry.getReference());
                        tx.put("date", entry.getDate());
                        tx.put("dateFormatted", entry.getDateFormatted());
                        tx.put("isSettled", entry.isSettled());
                        return tx;
                    })
                    .collect(Collectors.toList());

            response.put("transactions", transactions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            byte[] bytes = ("Fehler beim Export: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<>(bytes, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/partners/{partnerId}/transactions/{entryId}/status")
    public ResponseEntity<?> updateTransactionStatus(@PathVariable Long partnerId,
            @PathVariable String entryId,
            @RequestBody Map<String, Object> payload) {
        try {
            String statusRaw = Objects.toString(payload.get("status"), "").trim();
            if (statusRaw.isEmpty()) {
                return badRequest("Status ist erforderlich.");
            }

            FinancialEntryStatus status = FinancialEntryStatus.valueOf(statusRaw.toUpperCase(Locale.ROOT));
            UUID uuid = UUID.fromString(entryId);

            TradingPartner updated = tradingPartnerService.updateFinancialEntryStatus(partnerId, uuid, status);
            return okWithFinancials(updated, "Transaktionsstatus aktualisiert");
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @GetMapping("/partners/{id}/export-statement")
    public ResponseEntity<byte[]> exportPartnerStatement(@PathVariable Long id) {
        try {
            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(id);
            if (partnerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            TradingPartner partner = partnerOpt.get();
            TradingPartner.FinancialOverview overview = partner.getFinancialOverview();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

            StringBuilder builder = new StringBuilder();
            builder.append("Kontoauszug für;").append(escapeCsv(partner.getName())).append('\n');
            builder.append("Erstellt am;").append(LocalDateTime.now().format(formatter)).append('\n');
            builder.append("Partner-ID;").append(partner.getId()).append('\n');
            builder.append("Partner-Typ;").append(partner.getType()).append('\n');
            builder.append("Status;").append(partner.getStatus()).append('\n');
            builder.append("Steuernummer;").append(escapeCsv(partner.getTaxId())).append('\n');
            builder.append('\n');
            builder.append("Offene Forderungen;").append(overview.getOpenClaims()).append('\n');
            builder.append("Beglichene Forderungen;").append(overview.getSettledClaims()).append('\n');
            builder.append("Offene Verbindlichkeiten;").append(overview.getOpenPayables()).append('\n');
            builder.append("Beglichene Verbindlichkeiten;").append(overview.getSettledPayables()).append('\n');
            builder.append("Transaktionen insgesamt;").append(overview.getTransactionCount()).append('\n');
            builder.append('\n');
            builder.append("Typ;Status;Betrag;Datum;Verwendungszweck;Referenz\n");

            partner.getFinancialEntries().stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator
                            .comparing((FinancialEntry entry) -> entry.getDate(),
                                    Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(FinancialEntry::getCreatedAt,
                                    Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed())
                    .forEach(entry -> builder
                            .append(entry.getType())
                            .append(';').append(entry.getStatus())
                            .append(';').append(entry.getAmount() != null ? entry.getAmount().toPlainString() : "0.00")
                            .append(';').append(entry.getDate() != null ? entry.getDate() : "-")
                            .append(';').append(escapeCsv(entry.getPurpose()))
                            .append(';').append(escapeCsv(entry.getReference()))
                            .append('\n'));

            byte[] bytes = builder.toString().getBytes(StandardCharsets.UTF_8);
            String filename = "kontoauszug-" + slugify(partner.getName()) + "-" + LocalDate.now() + ".csv";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            headers.setContentLength(bytes.length);

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            byte[] errorBytes = ("Fehler beim Export: "
                    + (e.getMessage() != null ? e.getMessage() : "Unbekannter Fehler"))
                    .getBytes(StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<>(errorBytes, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Contact Management Endpoints
    @PostMapping("/partners/{id}/contacts")
    public ResponseEntity<?> addContact(@PathVariable Long id, @RequestBody Contact contact) {
        try {
            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(id);
            if (partnerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Contact normalized = normalizeContact(contact);
            if (normalized == null) {
                return badRequest(
                        "Kontaktdaten sind unvollständig. Bitte mindestens Name, E-Mail oder Telefon angeben.");
            }

            TradingPartner partner = partnerOpt.get();
            List<Contact> contacts = new ArrayList<>(partner.getContacts());
            contacts.add(normalized);
            partner.setContacts(contacts);

            TradingPartner updated = tradingPartnerService.updatePartner(id, partner);
            return okWithContacts(updated, "Kontakt erfolgreich hinzugefügt");
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @PutMapping("/partners/{id}/contacts/{contactIndex}")
    public ResponseEntity<?> updateContact(@PathVariable Long id, @PathVariable int contactIndex,
            @RequestBody Contact contact) {
        try {
            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(id);
            if (partnerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Contact normalized = normalizeContact(contact);
            if (normalized == null) {
                return badRequest(
                        "Kontaktdaten sind unvollständig. Bitte mindestens Name, E-Mail oder Telefon angeben.");
            }

            TradingPartner partner = partnerOpt.get();
            List<Contact> contacts = new ArrayList<>(partner.getContacts());

            if (contactIndex < 0 || contactIndex >= contacts.size()) {
                return badRequest("Kontaktindex ist ungültig.");
            }

            contacts.set(contactIndex, normalized);
            partner.setContacts(contacts);

            TradingPartner updated = tradingPartnerService.updatePartner(id, partner);
            return okWithContacts(updated, "Kontakt erfolgreich aktualisiert");
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @DeleteMapping("/partners/{id}/contacts/{contactIndex}")
    public ResponseEntity<?> deleteContact(@PathVariable Long id, @PathVariable int contactIndex) {
        try {
            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(id);
            if (partnerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            TradingPartner partner = partnerOpt.get();
            List<Contact> contacts = new ArrayList<>(partner.getContacts());

            if (contactIndex < 0 || contactIndex >= contacts.size()) {
                return badRequest("Kontaktindex ist ungültig.");
            }

            contacts.remove(contactIndex);
            partner.setContacts(contacts);

            TradingPartner updated = tradingPartnerService.updatePartner(id, partner);
            return okWithContacts(updated, "Kontakt erfolgreich gelöscht");
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    // Address Management Endpoints
    @PostMapping("/partners/{id}/addresses")
    public ResponseEntity<?> addAddress(@PathVariable Long id, @RequestBody Address address) {
        try {
            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(id);
            if (partnerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Address normalized = normalizeAddress(address);
            if (normalized == null) {
                return badRequest("Adresse ist unvollständig. Straße und Stadt sind Pflichtfelder.");
            }

            TradingPartner partner = partnerOpt.get();
            List<Address> addresses = new ArrayList<>(partner.getAddresses());
            addresses.add(normalized);
            partner.setAddresses(addresses);

            TradingPartner updated = tradingPartnerService.updatePartner(id, partner);
            return okWithAddresses(updated, "Adresse erfolgreich hinzugefügt");
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @PutMapping("/partners/{id}/addresses/{addressIndex}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @PathVariable int addressIndex,
            @RequestBody Address address) {
        try {
            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(id);
            if (partnerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Address normalized = normalizeAddress(address);
            if (normalized == null) {
                return badRequest("Adresse ist unvollständig. Straße und Stadt sind Pflichtfelder.");
            }

            TradingPartner partner = partnerOpt.get();
            List<Address> addresses = new ArrayList<>(partner.getAddresses());

            if (addressIndex < 0 || addressIndex >= addresses.size()) {
                return badRequest("Adressindex ist ungültig.");
            }

            addresses.set(addressIndex, normalized);
            partner.setAddresses(addresses);

            TradingPartner updated = tradingPartnerService.updatePartner(id, partner);
            return okWithAddresses(updated, "Adresse erfolgreich aktualisiert");
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @DeleteMapping("/partners/{id}/addresses/{addressIndex}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id, @PathVariable int addressIndex) {
        try {
            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(id);
            if (partnerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            TradingPartner partner = partnerOpt.get();
            List<Address> addresses = new ArrayList<>(partner.getAddresses());

            if (addressIndex < 0 || addressIndex >= addresses.size()) {
                return badRequest("Adressindex ist ungültig.");
            }

            addresses.remove(addressIndex);
            partner.setAddresses(addresses);

            TradingPartner updated = tradingPartnerService.updatePartner(id, partner);
            return okWithAddresses(updated, "Adresse erfolgreich gelöscht");
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    private ResponseEntity<Map<String, Object>> okWithFinancials(TradingPartner partner, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("financialOverview", partner.getFinancialOverview());
        response.put("transactions", partner.getFinancialEntries());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> okWithContacts(TradingPartner partner, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("contacts", partner.getContacts());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> okWithAddresses(TradingPartner partner, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("addresses", partner.getAddresses());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> badRequest(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<Map<String, Object>> errorResponse(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String sanitized = value.replaceAll("[\\r\\n]+", " ").trim();
        if (sanitized.contains(";") || sanitized.contains("\"")) {
            sanitized = '"' + sanitized.replace("\"", "\"\"") + '"';
        }
        return sanitized;
    }

    private String slugify(String value) {
        if (value == null || value.isBlank()) {
            return "partner";
        }
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("-+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private Contact normalizeContact(Contact incoming) {
        if (incoming == null) {
            return null;
        }

        Contact contact = new Contact();
        contact.setName(clean(incoming.getName()));
        contact.setRole(clean(incoming.getRole()));
        contact.setEmail(clean(incoming.getEmail()));
        contact.setPhone(clean(incoming.getPhone()));

        if (contact.getName() == null && contact.getEmail() == null && contact.getPhone() == null) {
            return null;
        }

        return contact;
    }

    private Address normalizeAddress(Address incoming) {
        if (incoming == null) {
            return null;
        }

        Address address = new Address();
        address.setType(clean(incoming.getType()));
        address.setStreet(clean(incoming.getStreet()));
        address.setZipCode(clean(incoming.getZipCode()));
        address.setCity(clean(incoming.getCity()));
        address.setCountry(clean(incoming.getCountry()));

        if (address.getStreet() == null || address.getCity() == null) {
            return null;
        }

        if (address.getType() == null) {
            address.setType("Hauptadresse");
        }

        if (address.getCountry() == null) {
            address.setCountry("Deutschland");
        }

        return address;
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
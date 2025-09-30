package org.iu.handelspartnern.spark.controller;

import org.iu.handelspartnern.common.dto.AddTradingPartnerDto;
import org.iu.handelspartnern.common.dto.TradingPartnerListDto;
import org.iu.handelspartnern.common.entity.*;
import org.iu.handelspartnern.spark.service.TradingPartnerService;
import org.iu.handelspartnern.spark.util.ErrorUtils;
import org.iu.handelspartnern.spark.util.JsonUtils;
import org.iu.handelspartnern.spark.util.ThymeleafContextUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.fasterxml.jackson.core.type.TypeReference;

import java.math.BigDecimal;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.Collections;
import java.util.stream.Collectors;

import static spark.Spark.*;

/**
 * Spark Java Controller - Exakte Kopie des Spring Controllers
 */
public class TradingPartnerController {

    private static final String JSON_TYPE = "application/json";

    private final TradingPartnerService service;
    private final TemplateEngine templateEngine;

    public TradingPartnerController(TradingPartnerService service, TemplateEngine templateEngine) {
        this.service = service;
        this.templateEngine = templateEngine;
        setupRoutes();
    }

    // Helper method for processing fragments
    private String processFragment(String templateName, WebContext context) {
        if (templateName.contains("::")) {
            String[] parts = templateName.split("::", 2);
            String baseTemplate = parts[0].trim();
            String fragment = parts[1].trim();
            return templateEngine.process(baseTemplate, Collections.singleton(fragment), context);
        }
        return templateEngine.process(templateName, context);
    }

    private void setupRoutes() {

        // ===== WEB ROUTES =====

        get("/", (req, res) -> {
            try {
                List<TradingPartnerListDto> partners = service.getAllPartners();

                WebContext context = ThymeleafContextUtils.createWebContext(req, res);
                context.setVariable("partners", partners);
                context.setVariable("partnerTypes", PartnerType.values());
                context.setVariable("partnerStatuses", PartnerStatus.values());

                return templateEngine.process("index", context);
            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        }); // Partner List - GET /partners (to match Spring Boot URL structure)
        get("/partners", (req, res) -> {
            try {
                String typeParam = req.queryParams("type");
                String statusParam = req.queryParams("status");
                String search = req.queryParams("search");

                PartnerType type = parsePartnerType(typeParam);
                PartnerStatus status = parsePartnerStatus(statusParam);

                List<TradingPartnerListDto> partners = service.getAllPartners(type, status, search);

                WebContext context = ThymeleafContextUtils.createWebContext(req, res);
                context.setVariable("partners", partners);

                return processFragment("fragments/partner-list :: partner-list", context);
            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });

        // Partner List - GET /partner (legacy support)
        get("/partner", (req, res) -> {
            res.redirect("/");
            return "";
        });

        // Add Partner Form - GET /partners/new (Spring Boot compatible URL)
        get("/partners/new", (req, res) -> {
            try {
                WebContext context = ThymeleafContextUtils.createWebContext(req, res);
                context.setVariable("types", PartnerType.values());
                context.setVariable("isNewPartner", true);
                // Use helper method for fragment processing
                return processFragment("fragments/partner-form :: partner-form", context);
            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });

        // Partner Detail - GET /partners/{id} (Spring Boot compatible URL)
        get("/partners/:id", (req, res) -> {
            try {
                Long id = Long.parseLong(req.params(":id"));
                Optional<TradingPartner> partnerOpt = service.getPartnerById(id);

                if (partnerOpt.isEmpty()) {
                    halt(404, "Partner nicht gefunden");
                }

                WebContext context = ThymeleafContextUtils.createWebContext(req, res);
                context.setVariable("partner", partnerOpt.get());
                context.setVariable("types", PartnerType.values());
                context.setVariable("statuses", PartnerStatus.values());
                context.setVariable("financialStatuses", FinancialEntryStatus.values());

                return processFragment("fragments/partner-detail :: partner-detail", context);
            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });

        // Partner Edit Form - GET /partners/{id}/edit (Spring Boot compatible URL)
        get("/partners/:id/edit", (req, res) -> {
            try {
                Long id = Long.parseLong(req.params(":id"));
                Optional<TradingPartner> partnerOpt = service.getPartnerById(id);

                if (partnerOpt.isEmpty()) {
                    halt(404, "Partner nicht gefunden");
                }

                WebContext context = ThymeleafContextUtils.createWebContext(req, res);
                context.setVariable("partner", partnerOpt.get());
                context.setVariable("types", PartnerType.values());
                context.setVariable("statuses", PartnerStatus.values());

                return processFragment("fragments/partner-form :: partner-form", context);
            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });

        // Create Partner - POST /partner
        post("/partner", (req, res) -> {
            try {
                String name = req.queryParams("name");
                String about = req.queryParams("about");
                String taxId = req.queryParams("taxId");
                String paymentTerms = req.queryParams("paymentTerms");
                String corporateImageUrl = req.queryParams("corporateImageUrl");
                String typeStr = req.queryParams("type");

                if (name == null || name.trim().isEmpty()) {
                    halt(400, "Name ist erforderlich");
                }

                PartnerType type = PartnerType.valueOf(typeStr);

                AddTradingPartnerDto dto = new AddTradingPartnerDto(
                        name,
                        Optional.ofNullable(about),
                        Optional.ofNullable(taxId),
                        Optional.ofNullable(paymentTerms),
                        Optional.ofNullable(corporateImageUrl),
                        type);

                TradingPartner partner = service.createPartner(dto);
                res.redirect("/partner/" + partner.getId());
                return "";

            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });

        // Create Partner - POST /partners (Spring Boot compatible URL)
        post("/partners", (req, res) -> {
            try {
                String name = req.queryParams("name");
                String about = req.queryParams("about");
                String taxId = req.queryParams("taxId");
                String paymentTerms = req.queryParams("paymentTerms");
                String corporateImageUrl = req.queryParams("corporateImageUrl");
                String typeStr = req.queryParams("type");

                if (name == null || name.trim().isEmpty()) {
                    halt(400, "Name ist erforderlich");
                }

                PartnerType type = PartnerType.valueOf(typeStr);

                AddTradingPartnerDto dto = new AddTradingPartnerDto(
                        name,
                        Optional.ofNullable(about),
                        Optional.ofNullable(taxId),
                        Optional.ofNullable(paymentTerms),
                        Optional.ofNullable(corporateImageUrl),
                        type);

                service.createPartner(dto);

                // Return updated partner list for HTMX requests
                WebContext context = ThymeleafContextUtils.createWebContext(req, res);
                List<TradingPartnerListDto> partners = service.getAllPartners();
                context.setVariable("partners", partners);
                return processFragment("fragments/partner-list :: partner-list", context);

            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });

        // Update Partner - POST /partner/{id}
        post("/partner/:id", (req, res) -> {
            try {
                Long id = Long.parseLong(req.params(":id"));

                Optional<TradingPartner> existingOpt = service.getPartnerById(id);
                if (existingOpt.isEmpty()) {
                    halt(404, "Partner nicht gefunden");
                }

                TradingPartner existing = existingOpt.get();

                // Update basic fields
                String name = req.queryParams("name");
                String about = req.queryParams("about");
                String taxId = req.queryParams("taxId");
                String paymentTerms = req.queryParams("paymentTerms");
                String corporateImageUrl = req.queryParams("corporateImageUrl");
                String typeStr = req.queryParams("type");
                String statusStr = req.queryParams("status");

                if (name != null)
                    existing.setName(name);
                if (about != null)
                    existing.setAbout(about);
                if (taxId != null)
                    existing.setTaxId(taxId);
                if (paymentTerms != null)
                    existing.setPaymentTerms(paymentTerms);
                if (corporateImageUrl != null)
                    existing.setCorporateImageUrl(corporateImageUrl);
                if (typeStr != null)
                    existing.setType(PartnerType.valueOf(typeStr));
                if (statusStr != null)
                    existing.setStatus(PartnerStatus.valueOf(statusStr));

                TradingPartner updated = service.updatePartner(id, existing);
                res.redirect("/partner/" + updated.getId());
                return "";

            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });

        // Update Partner - PUT /partners/{id} (Spring Boot compatible URL)
        put("/partners/:id", (req, res) -> {
            try {
                Long id = Long.parseLong(req.params(":id"));

                Optional<TradingPartner> existingOpt = service.getPartnerById(id);
                if (existingOpt.isEmpty()) {
                    halt(404, "Partner nicht gefunden");
                }

                TradingPartner existing = existingOpt.get();

                // Update basic fields
                String name = req.queryParams("name");
                String about = req.queryParams("about");
                String taxId = req.queryParams("taxId");
                String paymentTerms = req.queryParams("paymentTerms");
                String corporateImageUrl = req.queryParams("corporateImageUrl");
                String typeStr = req.queryParams("type");
                String statusStr = req.queryParams("status");

                if (name != null)
                    existing.setName(name);
                if (about != null)
                    existing.setAbout(about);
                if (taxId != null)
                    existing.setTaxId(taxId);
                if (paymentTerms != null)
                    existing.setPaymentTerms(paymentTerms);
                if (corporateImageUrl != null)
                    existing.setCorporateImageUrl(corporateImageUrl);
                if (typeStr != null)
                    existing.setType(PartnerType.valueOf(typeStr));
                if (statusStr != null)
                    existing.setStatus(PartnerStatus.valueOf(statusStr));

                TradingPartner updated = service.updatePartner(id, existing);

                // Return updated partner details for HTMX requests
                WebContext context = ThymeleafContextUtils.createWebContext(req, res);
                context.setVariable("partner", updated);
                context.setVariable("types", PartnerType.values());
                context.setVariable("statuses", PartnerStatus.values());
                context.setVariable("financialStatuses", FinancialEntryStatus.values());
                return processFragment("fragments/partner-detail :: partner-detail", context);

            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });

        // Delete Partner - POST /partner/{id}/delete
        post("/partner/:id/delete", (req, res) -> {
            try {
                Long id = Long.parseLong(req.params(":id"));
                service.deletePartner(id);
                res.redirect("/partner");
                return "";
            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });

        // Delete Partner - DELETE /partners/{id} (Spring Boot compatible URL)
        delete("/partners/:id", (req, res) -> {
            try {
                Long id = Long.parseLong(req.params(":id"));
                service.deletePartner(id);

                // Return updated partner list for HTMX requests
                WebContext context = ThymeleafContextUtils.createWebContext(req, res);
                List<TradingPartnerListDto> partners = service.getAllPartners();
                context.setVariable("partners", partners);
                return processFragment("fragments/partner-list :: partner-list", context);

            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });

        // ===== FINANCIAL ENTRIES =====

        // Add Financial Entry - POST /partner/{id}/financial
        post("/partner/:id/financial", (req, res) -> {
            try {
                Long partnerId = Long.parseLong(req.params(":id"));

                String purpose = req.queryParams("purpose");
                String amountStr = req.queryParams("amount");
                String typeStr = req.queryParams("type");
                String dateStr = req.queryParams("date");

                BigDecimal amount = new BigDecimal(amountStr);
                FinancialEntryType type = FinancialEntryType.valueOf(typeStr);
                LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();

                FinancialEntry entry = new FinancialEntry();
                entry.setPurpose(purpose);
                entry.setAmount(amount);
                entry.setType(type);
                entry.setDate(date);
                entry.setStatus(FinancialEntryStatus.OPEN);

                service.addFinancialEntry(partnerId, entry);
                res.redirect("/partner/" + partnerId);
                return "";

            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });

        // Update Financial Entry Status - POST /partner/{id}/financial/{entryId}/status
        post("/partner/:id/financial/:entryId/status", (req, res) -> {
            try {
                Long partnerId = Long.parseLong(req.params(":id"));
                UUID entryId = UUID.fromString(req.params(":entryId"));
                String statusStr = req.queryParams("status");

                FinancialEntryStatus status = FinancialEntryStatus.valueOf(statusStr);
                service.updateFinancialEntryStatus(partnerId, entryId, status);

                res.redirect("/partner/" + partnerId);
                return "";

            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });

        // ===== API ROUTES =====

        get("/api/partners", (req, res) -> {
            try {
                res.type(JSON_TYPE);

                String typeParam = req.queryParams("type");
                String statusParam = req.queryParams("status");
                String search = req.queryParams("search");

                PartnerType type = parsePartnerType(typeParam);
                PartnerStatus status = parsePartnerStatus(statusParam);

                List<TradingPartnerListDto> partners = service.getAllPartners(type, status, search);
                return JsonUtils.write(partners);
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        get("/api/partners/:id", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Long id = Long.parseLong(req.params(":id"));

                Optional<TradingPartner> partner = service.getPartnerById(id);
                if (partner.isEmpty()) {
                    res.status(404);
                    return errorJson("Partner nicht gefunden");
                }

                return JsonUtils.write(partner.get());
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        post("/api/partners", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Map<String, Object> payload = readPayload(req.body());

                String name = requireText((String) payload.get("name"), "Name ist erforderlich");
                String typeRaw = requireText((String) payload.get("type"), "Partner-Typ ist erforderlich");

                PartnerType type = PartnerType.valueOf(typeRaw.trim().toUpperCase(Locale.ROOT));

                AddTradingPartnerDto dto = new AddTradingPartnerDto(
                        name,
                        Optional.ofNullable(cleanText((String) payload.get("about"))),
                        Optional.ofNullable(cleanText((String) payload.get("taxId"))),
                        Optional.ofNullable(cleanText((String) payload.get("paymentTerms"))),
                        Optional.ofNullable(cleanText((String) payload.get("corporateImageUrl"))),
                        type);

                TradingPartner partner = service.createPartner(dto);
                applyPartnerExtensions(partner, payload);
                TradingPartner saved = service.updatePartner(partner.getId(), partner);

                res.status(201);
                return JsonUtils.write(saved);
            } catch (IllegalArgumentException e) {
                res.status(400);
                return errorJson(e.getMessage());
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        put("/api/partners/:id", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Long id = Long.parseLong(req.params(":id"));

                Optional<TradingPartner> existingOpt = service.getPartnerById(id);
                if (existingOpt.isEmpty()) {
                    res.status(404);
                    return errorJson("Partner nicht gefunden");
                }

                Map<String, Object> payload = readPayload(req.body());
                TradingPartner existing = existingOpt.get();

                Optional.ofNullable(cleanText((String) payload.get("name"))).ifPresent(existing::setName);

                Optional.ofNullable(cleanText((String) payload.get("type")))
                        .map(value -> PartnerType.valueOf(value.toUpperCase(Locale.ROOT)))
                        .ifPresent(existing::setType);

                Optional.ofNullable(cleanText((String) payload.get("status")))
                        .map(value -> PartnerStatus.valueOf(value.toUpperCase(Locale.ROOT)))
                        .ifPresent(existing::setStatus);

                Optional.ofNullable(cleanText((String) payload.get("taxId"))).ifPresent(existing::setTaxId);
                Optional.ofNullable(cleanText((String) payload.get("paymentTerms")))
                        .ifPresent(existing::setPaymentTerms);
                Optional.ofNullable(cleanText((String) payload.get("about"))).ifPresent(existing::setAbout);
                Optional.ofNullable(cleanText((String) payload.get("corporateImageUrl")))
                        .ifPresent(existing::setCorporateImageUrl);

                applyPartnerExtensions(existing, payload);

                TradingPartner updated = service.updatePartner(id, existing);
                return JsonUtils.write(updated);
            } catch (IllegalArgumentException e) {
                res.status(400);
                return errorJson(e.getMessage());
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        delete("/api/partners/:id", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Long id = Long.parseLong(req.params(":id"));

                service.deletePartner(id);
                return JsonUtils.write(Map.of("success", true));
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        get("/api/partners/:id/balance", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Long id = Long.parseLong(req.params(":id"));
                Map<String, BigDecimal> balance = service.getPartnerBalance(id);
                return JsonUtils.write(balance);
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        post("/api/partners/:id/contacts", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Long partnerId = Long.parseLong(req.params(":id"));
                Contact incoming = JsonUtils.readValue(req.body(), Contact.class);
                Contact contact = normalizeContact(incoming);
                if (contact == null) {
                    res.status(400);
                    return errorJson(
                            "Kontaktdaten sind unvollständig. Bitte mindestens Name, E-Mail oder Telefon angeben.");
                }

                TradingPartner updated = service.addContact(partnerId, contact);
                return okWithContacts(updated, "Kontakt erfolgreich hinzugefügt");
            } catch (IllegalArgumentException e) {
                res.status(400);
                return errorJson(e.getMessage());
            } catch (RuntimeException e) {
                res.status(404);
                return errorJson(e.getMessage());
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        put("/api/partners/:id/contacts/:index", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Long partnerId = Long.parseLong(req.params(":id"));
                int index = Integer.parseInt(req.params(":index"));
                Contact incoming = JsonUtils.readValue(req.body(), Contact.class);
                Contact contact = normalizeContact(incoming);
                if (contact == null) {
                    res.status(400);
                    return errorJson(
                            "Kontaktdaten sind unvollständig. Bitte mindestens Name, E-Mail oder Telefon angeben.");
                }

                TradingPartner updated = service.updateContact(partnerId, index, contact);
                return okWithContacts(updated, "Kontakt erfolgreich aktualisiert");
            } catch (IllegalArgumentException e) {
                res.status(400);
                return errorJson(e.getMessage());
            } catch (RuntimeException e) {
                res.status(404);
                return errorJson(e.getMessage());
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        delete("/api/partners/:id/contacts/:index", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Long partnerId = Long.parseLong(req.params(":id"));
                int index = Integer.parseInt(req.params(":index"));

                TradingPartner updated = service.deleteContact(partnerId, index);
                return okWithContacts(updated, "Kontakt erfolgreich gelöscht");
            } catch (IllegalArgumentException e) {
                res.status(400);
                return errorJson(e.getMessage());
            } catch (RuntimeException e) {
                res.status(404);
                return errorJson(e.getMessage());
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        post("/api/partners/:id/addresses", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Long partnerId = Long.parseLong(req.params(":id"));
                Address incoming = JsonUtils.readValue(req.body(), Address.class);
                Address address = normalizeAddress(incoming);
                if (address == null) {
                    res.status(400);
                    return errorJson("Adresse ist unvollständig. Straße und Stadt sind Pflichtfelder.");
                }

                TradingPartner updated = service.addAddress(partnerId, address);
                return okWithAddresses(updated, "Adresse erfolgreich hinzugefügt");
            } catch (IllegalArgumentException e) {
                res.status(400);
                return errorJson(e.getMessage());
            } catch (RuntimeException e) {
                res.status(404);
                return errorJson(e.getMessage());
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        put("/api/partners/:id/addresses/:index", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Long partnerId = Long.parseLong(req.params(":id"));
                int index = Integer.parseInt(req.params(":index"));
                Address incoming = JsonUtils.readValue(req.body(), Address.class);
                Address address = normalizeAddress(incoming);
                if (address == null) {
                    res.status(400);
                    return errorJson("Adresse ist unvollständig. Straße und Stadt sind Pflichtfelder.");
                }

                TradingPartner updated = service.updateAddress(partnerId, index, address);
                return okWithAddresses(updated, "Adresse erfolgreich aktualisiert");
            } catch (IllegalArgumentException e) {
                res.status(400);
                return errorJson(e.getMessage());
            } catch (RuntimeException e) {
                res.status(404);
                return errorJson(e.getMessage());
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        delete("/api/partners/:id/addresses/:index", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Long partnerId = Long.parseLong(req.params(":id"));
                int index = Integer.parseInt(req.params(":index"));

                TradingPartner updated = service.deleteAddress(partnerId, index);
                return okWithAddresses(updated, "Adresse erfolgreich gelöscht");
            } catch (IllegalArgumentException e) {
                res.status(400);
                return errorJson(e.getMessage());
            } catch (RuntimeException e) {
                res.status(404);
                return errorJson(e.getMessage());
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        get("/api/partners/:id/transactions", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Long partnerId = Long.parseLong(req.params(":id"));

                Optional<TradingPartner> partnerOpt = service.getPartnerById(partnerId);
                if (partnerOpt.isEmpty()) {
                    res.status(404);
                    return errorJson("Partner nicht gefunden");
                }

                TradingPartner partner = partnerOpt.get();
                TradingPartner.FinancialOverview overview = partner.getFinancialOverview();

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

                Map<String, Object> response = new HashMap<>();
                response.put("overview", overviewMap);
                response.put("partner", Map.of(
                        "id", partner.getId(),
                        "name", partner.getName(),
                        "taxId", partner.getTaxId(),
                        "status", partner.getStatus(),
                        "type", partner.getType()));
                response.put("transactions", transactions);

                return JsonUtils.write(response);
            } catch (RuntimeException e) {
                res.status(404);
                return errorJson(e.getMessage());
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        post("/api/partners/financial-transaction", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Map<String, Object> payload = readPayload(req.body());

                Long partnerId = parseRequiredLong(payload.get("partnerId"), "Partner ID fehlt.");

                String typeRaw = requireText(Objects.toString(payload.get("type"), null),
                        "Transaktionstyp ist erforderlich.");
                FinancialEntryType entryType = FinancialEntryType.valueOf(typeRaw.toUpperCase(Locale.ROOT));

                BigDecimal amount = parseAmount(Objects.toString(payload.get("amount"), "0"));
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    res.status(400);
                    return errorJson("Betrag muss größer als 0 sein.");
                }

                FinancialEntry entry = new FinancialEntry(
                        entryType,
                        FinancialEntryStatus.OPEN,
                        amount,
                        Objects.toString(payload.get("purpose"), ""),
                        Objects.toString(payload.get("reference"), ""),
                        parseDate(Objects.toString(payload.get("date"), null)));

                TradingPartner updated = service.addFinancialEntry(partnerId, entry);
                return okWithFinancials(updated, "Transaktion erfolgreich erfasst.");
            } catch (IllegalArgumentException e) {
                res.status(400);
                return errorJson(e.getMessage());
            } catch (RuntimeException e) {
                res.status(404);
                return errorJson(e.getMessage());
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        patch("/api/partners/:partnerId/transactions/:entryId/status", (req, res) -> {
            try {
                res.type(JSON_TYPE);
                Long partnerId = Long.parseLong(req.params(":partnerId"));
                UUID entryId = UUID.fromString(req.params(":entryId"));

                Map<String, Object> payload = readPayload(req.body());
                String statusRaw = requireText(Objects.toString(payload.get("status"), null),
                        "Status ist erforderlich.");

                FinancialEntryStatus status = FinancialEntryStatus.valueOf(statusRaw.toUpperCase(Locale.ROOT));
                TradingPartner updated = service.updateFinancialEntryStatus(partnerId, entryId, status);
                return okWithFinancials(updated, "Transaktionsstatus aktualisiert");
            } catch (IllegalArgumentException e) {
                res.status(400);
                return errorJson(e.getMessage());
            } catch (RuntimeException e) {
                res.status(404);
                return errorJson(e.getMessage());
            } catch (Exception e) {
                res.status(500);
                return errorJson("Interner Fehler: " + e.getMessage());
            }
        });

        // ===== FRAGMENT ROUTES (für HTMX) =====

        // Partner List Fragment - GET /fragments/partner-list
        get("/fragments/partner-list", (req, res) -> {
            try {
                String typeParam = req.queryParams("type");
                String statusParam = req.queryParams("status");
                String search = req.queryParams("search");

                PartnerType type = parsePartnerType(typeParam);
                PartnerStatus status = parsePartnerStatus(statusParam);

                List<TradingPartnerListDto> partners = service.getAllPartners(type, status, search);

                WebContext context = ThymeleafContextUtils.createWebContext(req, res);
                context.setVariable("partners", partners);

                return processFragment("fragments/partner-list :: partner-list", context);
            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });

        // Partner Detail Fragment - GET /fragments/partner/{id}
        get("/fragments/partner/:id", (req, res) -> {
            try {
                Long id = Long.parseLong(req.params(":id"));
                Optional<TradingPartner> partnerOpt = service.getPartnerById(id);

                if (partnerOpt.isEmpty()) {
                    halt(404, "Partner nicht gefunden");
                }

                WebContext context = ThymeleafContextUtils.createWebContext(req, res);
                context.setVariable("partner", partnerOpt.get());
                context.setVariable("types", PartnerType.values());
                context.setVariable("statuses", PartnerStatus.values());

                return processFragment("fragments/partner-detail :: partner-detail", context);
            } catch (Exception e) {
                return ErrorUtils.handleException(e, templateEngine, req, res);
            }
        });
    }

    // ===== HELPER METHODS =====

    private PartnerType parsePartnerType(String typeParam) {
        if (typeParam == null || typeParam.trim().isEmpty()) {
            return null;
        }
        try {
            return PartnerType.valueOf(typeParam.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private PartnerStatus parsePartnerStatus(String statusParam) {
        if (statusParam == null || statusParam.trim().isEmpty()) {
            return null;
        }
        try {
            return PartnerStatus.valueOf(statusParam.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Map<String, Object> readPayload(String body) throws IOException {
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Request body ist erforderlich.");
        }
        return JsonUtils.readToMap(body);
    }

    private String requireText(String raw, String message) {
        String cleaned = cleanText(raw);
        if (cleaned == null) {
            throw new IllegalArgumentException(message);
        }
        return cleaned;
    }

    private String cleanText(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void applyPartnerExtensions(TradingPartner partner, Map<String, Object> payload) {
        Object statusRaw = payload.get("status");
        if (statusRaw != null) {
            String statusText = statusRaw.toString().trim();
            if (!statusText.isEmpty()) {
                partner.setStatus(PartnerStatus.valueOf(statusText.toUpperCase(Locale.ROOT)));
            }
        }

        Object claimsRaw = payload.get("claims");
        if (claimsRaw != null) {
            partner.setClaims(parseBigDecimal(claimsRaw.toString(), partner.getClaims()));
        }

        Object payablesRaw = Optional.ofNullable(payload.get("payable")).orElse(payload.get("payables"));
        if (payablesRaw != null) {
            partner.setPayable(parseBigDecimal(payablesRaw.toString(), partner.getPayable()));
        }

        Object contactsRaw = payload.get("contacts");
        if (contactsRaw != null) {
            List<Contact> contacts = JsonUtils.mapper().convertValue(contactsRaw, new TypeReference<List<Contact>>() {
            });
            List<Contact> normalized = contacts.stream()
                    .map(this::normalizeContact)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            partner.setContacts(normalized);
        }

        Object addressesRaw = payload.get("addresses");
        if (addressesRaw != null) {
            List<Address> addresses = JsonUtils.mapper().convertValue(addressesRaw, new TypeReference<List<Address>>() {
            });
            List<Address> normalized = addresses.stream()
                    .map(this::normalizeAddress)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            partner.setAddresses(normalized);
        }
    }

    private String errorJson(String message) {
        return JsonUtils.write(Map.of(
                "success", false,
                "message", message));
    }

    private String okWithContacts(TradingPartner partner, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("contacts", partner.getContacts());
        return JsonUtils.write(response);
    }

    private String okWithAddresses(TradingPartner partner, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("addresses", partner.getAddresses());
        return JsonUtils.write(response);
    }

    private String okWithFinancials(TradingPartner partner, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("financialOverview", partner.getFinancialOverview());
        response.put("transactions", partner.getFinancialEntries());
        return JsonUtils.write(response);
    }

    private Long parseRequiredLong(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        try {
            if (value instanceof Number number) {
                return number.longValue();
            }
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(message);
        }
    }

    private BigDecimal parseAmount(String raw) {
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ungültiger Betrag: " + raw);
        }
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return LocalDate.now();
        }
        return LocalDate.parse(raw);
    }

    private BigDecimal parseBigDecimal(String raw, BigDecimal fallback) {
        if (raw == null) {
            return fallback;
        }
        try {
            return new BigDecimal(raw.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
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
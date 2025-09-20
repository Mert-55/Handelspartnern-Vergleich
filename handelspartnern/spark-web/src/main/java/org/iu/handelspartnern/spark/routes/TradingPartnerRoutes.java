package org.iu.handelspartnern.spark.routes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.iu.handelspartnern.common.entity.PartnerStatus;
import org.iu.handelspartnern.common.entity.PartnerType;
import org.iu.handelspartnern.common.entity.TradingPartner;
import org.iu.handelspartnern.common.dto.AddTradingPartnerDto;
import org.iu.handelspartnern.spark.config.ThymeleafConfig;
import org.iu.handelspartnern.spark.service.TradingPartnerService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.*;

/**
 * Spark Java Routes - Manual Route Definitions (Im Gegensatz zu
 * Spring's @Controller und @GetMapping)
 */
public class TradingPartnerRoutes {

    private final TradingPartnerService tradingPartnerService;
    private final ThymeleafConfig thymeleafConfig;
    private final Gson gson;

    public TradingPartnerRoutes(TradingPartnerService service, ThymeleafConfig thymeleaf) {
        this.tradingPartnerService = service;
        this.thymeleafConfig = thymeleaf;
        this.gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    }

    public void registerRoutes() {

        // ========== HTML ROUTES (Thymeleaf) ==========

        // Homepage Route - Equivalent zu Spring's @GetMapping("/")
        get("/", this::homepage);

        // Partner Details Route
        get("/partners/:id", this::getPartnerDetails);

        // ========== API ROUTES (JSON) ==========

        // Get All Partners (with filters) - API Route
        get("/api/partners", this::getAllPartnersApi);

        // Get Single Partner - API Route
        get("/api/partners/:id", this::getPartnerByIdApi);

        // Create Partner - API Route
        post("/api/partners", this::createPartnerApi);

        // Update Partner - API Route
        put("/api/partners/:id", this::updatePartnerApi);

        // Delete Partner - API Route
        delete("/api/partners/:id", this::deletePartnerApi);

        // ========== STATIC CONTENT ==========

        // Exception handling
        exception(RuntimeException.class, this::handleException);

        // 404 Handler
        notFound(this::handle404);

    }

    // ========== ROUTE HANDLERS ==========

    /**
     * Homepage - Renders index.html with partners list Equivalent zu Spring
     * Controller's index() method
     */
    private String homepage(Request req, Response res) {
        try {
            // Parse query parameters (same as Spring's @RequestParam)
            PartnerType type = parsePartnerType(req.queryParams("type"));
            PartnerStatus status = parsePartnerStatus(req.queryParams("status"));
            String search = req.queryParams("search");

            // Create model for Thymeleaf
            Map<String, Object> model = new HashMap<>();
            model.put("partners", tradingPartnerService.getAllPartners(type, status, search));
            model.put("partnerTypes", PartnerType.values());
            model.put("partnerStatuses", PartnerStatus.values());
            model.put("selectedType", type);
            model.put("selectedStatus", status);
            model.put("searchQuery", search);

            // Set content type
            res.type("text/html; charset=utf-8");

            return thymeleafConfig.render("index", model);

        } catch (Exception e) {
            System.err.println("Error in homepage route: " + e.getMessage());
            e.printStackTrace();
            res.status(500);
            return createErrorResponse("Internal Server Error", e.getMessage());
        }
    }

    /**
     * Partner Details - Shows detailed partner view
     */
    private String getPartnerDetails(Request req, Response res) {
        try {
            Long id = Long.parseLong(req.params(":id"));
            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(id);

            if (partnerOpt.isPresent()) {
                Map<String, Object> model = new HashMap<>();
                model.put("partner", partnerOpt.get());

                res.type("text/html; charset=utf-8");
                return thymeleafConfig.render("partner-details", model);
            } else {
                res.status(404);
                return thymeleafConfig.render("404");
            }

        } catch (NumberFormatException e) {
            res.status(400);
            return createErrorResponse("Bad Request", "Invalid partner ID");
        } catch (Exception e) {
            res.status(500);
            return createErrorResponse("Internal Server Error", e.getMessage());
        }
    }

    /**
     * API: Get All Partners (JSON Response)
     */
    private String getAllPartnersApi(Request req, Response res) {
        try {
            PartnerType type = parsePartnerType(req.queryParams("type"));
            PartnerStatus status = parsePartnerStatus(req.queryParams("status"));
            String search = req.queryParams("search");

            res.type("application/json");
            return gson.toJson(tradingPartnerService.getAllPartners(type, status, search));

        } catch (Exception e) {
            res.status(500);
            res.type("application/json");
            return createJsonError("Internal Server Error", e.getMessage());
        }
    }

    /**
     * API: Get Partner by ID (JSON Response)
     */
    private String getPartnerByIdApi(Request req, Response res) {
        try {
            Long id = Long.parseLong(req.params(":id"));
            Optional<TradingPartner> partnerOpt = tradingPartnerService.getPartnerById(id);

            if (partnerOpt.isPresent()) {
                res.type("application/json");
                return gson.toJson(partnerOpt.get());
            } else {
                res.status(404);
                res.type("application/json");
                return createJsonError("Not Found", "Partner not found");
            }

        } catch (NumberFormatException e) {
            res.status(400);
            res.type("application/json");
            return createJsonError("Bad Request", "Invalid partner ID");
        } catch (Exception e) {
            res.status(500);
            res.type("application/json");
            return createJsonError("Internal Server Error", e.getMessage());
        }
    }

    /**
     * API: Create Partner (JSON Response)
     */
    private String createPartnerApi(Request req, Response res) {
        try {
            // Parse JSON request body (equivalent zu Spring's @RequestBody)
            AddTradingPartnerDto dto = gson.fromJson(req.body(), AddTradingPartnerDto.class);

            if (dto == null || dto.name() == null || dto.name().trim().isEmpty()) {
                res.status(400);
                res.type("application/json");
                return createJsonError("Bad Request", "Name is required");
            }

            TradingPartner created = tradingPartnerService.createPartner(dto);

            res.status(201); // Created
            res.type("application/json");
            return gson.toJson(created);

        } catch (Exception e) {
            res.status(500);
            res.type("application/json");
            return createJsonError("Internal Server Error", e.getMessage());
        }
    }

    /**
     * API: Update Partner (JSON Response)
     */
    private String updatePartnerApi(Request req, Response res) {
        try {
            Long id = Long.parseLong(req.params(":id"));
            TradingPartner updatedPartner = gson.fromJson(req.body(), TradingPartner.class);

            if (updatedPartner == null) {
                res.status(400);
                res.type("application/json");
                return createJsonError("Bad Request", "Request body is required");
            }

            TradingPartner updated = tradingPartnerService.updatePartner(id, updatedPartner);

            res.type("application/json");
            return gson.toJson(updated);

        } catch (NumberFormatException e) {
            res.status(400);
            res.type("application/json");
            return createJsonError("Bad Request", "Invalid partner ID");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                res.status(404);
                res.type("application/json");
                return createJsonError("Not Found", e.getMessage());
            } else {
                res.status(500);
                res.type("application/json");
                return createJsonError("Internal Server Error", e.getMessage());
            }
        }
    }

    /**
     * API: Delete Partner
     */
    private String deletePartnerApi(Request req, Response res) {
        try {
            Long id = Long.parseLong(req.params(":id"));
            tradingPartnerService.deletePartner(id);

            res.status(204); // No Content
            return "";

        } catch (NumberFormatException e) {
            res.status(400);
            res.type("application/json");
            return createJsonError("Bad Request", "Invalid partner ID");
        } catch (Exception e) {
            res.status(500);
            res.type("application/json");
            return createJsonError("Internal Server Error", e.getMessage());
        }
    }

    // ========== HELPER METHODS ==========

    private PartnerType parsePartnerType(String typeStr) {
        if (typeStr == null || typeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return PartnerType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private PartnerStatus parsePartnerStatus(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return null;
        }
        try {
            return PartnerStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String createErrorResponse(String title, String message) {
        return String.format("<html><head><title>%s</title></head>" + "<body><h1>%s</h1><p>%s</p></body></html>", title,
                title, message);
    }

    private String createJsonError(String error, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        return gson.toJson(errorResponse);
    }

    // ========== EXCEPTION HANDLERS ==========

    private void handleException(RuntimeException e, Request req, Response res) {
        System.err.println("Unhandled exception in route: " + req.requestMethod() + " " + req.uri());
        e.printStackTrace();

        res.status(500);

        if (req.headers("Accept") != null && req.headers("Accept").contains("application/json")) {
            res.type("application/json");
            res.body(createJsonError("Internal Server Error", "An unexpected error occurred"));
        } else {
            res.type("text/html");
            res.body(createErrorResponse("Internal Server Error", "An unexpected error occurred"));
        }
    }

    private String handle404(Request req, Response res) {
        if (req.headers("Accept") != null && req.headers("Accept").contains("application/json")) {
            res.type("application/json");
            return createJsonError("Not Found", "The requested resource was not found");
        } else {
            res.type("text/html");
            return createErrorResponse("Not Found", "The requested page was not found");
        }
    }

    // ========== JSON ADAPTERS ==========

    /**
     * Custom Gson adapter for LocalDateTime serialization
     */
    private static class LocalDateTimeAdapter implements com.google.gson.JsonSerializer<LocalDateTime> {
        @Override
        public com.google.gson.JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc,
                com.google.gson.JsonSerializationContext context) {
            return new com.google.gson.JsonPrimitive(src.toString());
        }
    }
}

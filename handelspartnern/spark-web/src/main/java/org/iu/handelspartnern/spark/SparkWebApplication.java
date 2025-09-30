package org.iu.handelspartnern.spark;

import org.iu.handelspartnern.spark.config.DatabaseConfig;
import org.iu.handelspartnern.spark.config.ThymeleafConfig;
import org.iu.handelspartnern.spark.controller.TradingPartnerController;
import org.iu.handelspartnern.spark.service.TradingPartnerService;
import org.iu.handelspartnern.spark.repository.TradingPartnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

/**
 * Spark Java Application Manual Configuration (Im Gegensatz zu Spring
 * Boot's @SpringBootApplication Auto-Configuration)
 */
public class SparkWebApplication {

    private static final Logger logger = LoggerFactory.getLogger(SparkWebApplication.class);

    public static void main(String[] args) {
        // Spark Java Manual Configuration - kein Auto-Config wie Spring Boot
        configureServer();

        // CORS Configuration
        configureCors();

        try {
            // Manual Dependency Setup - No DI Container wie Spring
            DatabaseConfig databaseConfig = new DatabaseConfig();
            databaseConfig.initialize();

            ThymeleafConfig thymeleafConfig = new ThymeleafConfig();

            TradingPartnerRepository repository = new TradingPartnerRepository(databaseConfig);
            TradingPartnerService service = new TradingPartnerService(repository);

            // Register Controller with Routes kein Component Scan wie Spring
            new TradingPartnerController(service,
                    thymeleafConfig.getTemplateEngine());

            // Health Check Endpoint - kein Actuator wie Spring Boot
            setupHealthCheck();

            // Graceful Shutdown Hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("🛑 Shutting down Spark Java Application...");
                databaseConfig.shutdown();
                stop();
            }));

            try {
                awaitInitialization();
                logger.info("Spark Java Application ignited!");
                logger.info("Health check: http://localhost:4568/health");
                logger.info("Homepage: http://localhost:4568/");
                logger.info("Port: 4568 (vs Spring Boot: 8080)");
            } catch (Exception initException) {
                logger.error("Failed to initialize Spark server: " + initException.getMessage());
                logger.info("Port 4568 might be in use. Trying alternative port 4569...");

                // Try alternative port
                stop();
                port(4569);
                awaitInitialization();
                logger.info("Spark Java Application started on alternative port!");
                logger.info("Homepage: http://localhost:4568/");
            }

        } catch (Exception e) {
            logger.error("Failed to start Spark application", e);
            logger.info("Try stopping other Java processes or use a different port");
            System.exit(1);
        }
    }

    private static void configureServer() {
        // Server Configuration Manual - kein application.yml wie Spring Boot
        port(4568);

        // Static files setup
        staticFiles.location("/static");
        staticFiles.expireTime(3600); // 1 hour cache

        // Embedded Jetty Configuration
        int maxThreads = 8;
        int minThreads = 2;
        int timeOutMillis = 30000;
        threadPool(maxThreads, minThreads, timeOutMillis);

        // Static Files Configuration
        staticFiles.location("/static"); // Same as Spring Boot
        staticFiles.expireTime(600); // 10 minutes cache
    }

    private static void configureCors() {
        // CORS Configuration (Manual - kein Spring Security wie Spring Boot)
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Accept");
            response.header("Access-Control-Max-Age", "3600");
        });

        // Handle CORS preflight requests
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });
    }

    private static void setupHealthCheck() {
        // Health Check Endpoint (Manual - kein Spring Boot Actuator)
        get("/health", (req, res) -> {
            res.type("application/json");
            return "{\n" + "  \"status\": \"UP\",\n" + "  \"framework\": \"Spark Java\",\n" + "  \"port\": 4568,\n"
                    + "  \"version\": \"1.0-SNAPSHOT\",\n" + "  \"timestamp\": \"" + java.time.LocalDateTime.now()
                    + "\",\n" + "  \"comparison\": \"vs Spring Boot on 8080\"\n" + "}";
        });

        // Info Endpoint
        get("/info", (req, res) -> {
            res.type("application/json");
            return "{\n" + "  \"app\": {\n" + "    \"name\": \"TradingPartner Spark Java\",\n"
                    + "    \"description\": \"Manual Configuration Framework Comparison\",\n"
                    + "    \"version\": \"1.0-SNAPSHOT\"\n" + "  },\n" + "  \"java\": {\n" + "    \"version\": \""
                    + System.getProperty("java.version") + "\",\n" + "    \"vendor\": \""
                    + System.getProperty("java.vendor") + "\"\n" + "  }\n" + "}";
        });
    }
}

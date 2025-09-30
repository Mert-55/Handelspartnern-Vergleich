package org.iu.handelspartnern.spark.config;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

/**
 * Manual Thymeleaf Configuration für Spark Java (Im Gegensatz zu Spring Boot's
 * Auto-Configuration)
 */
public class ThymeleafConfig {

    private final TemplateEngine templateEngine;

    public ThymeleafConfig() {
        this.templateEngine = createTemplateEngine();
    }

    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    private TemplateEngine createTemplateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(createTemplateResolver());
        engine.setEnableSpringELCompiler(true);
        engine.addDialect(new Java8TimeDialect());

        // Enable fragment processing
        engine.setMessageResolver(new org.thymeleaf.messageresolver.StandardMessageResolver());

        return engine;
    }

    private ITemplateResolver createTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/"); // lädt aus common-template JAR under /templates/
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        return resolver;
    }

    public String render(String templateName, Map<String, Object> model) {
        try {
            // Create Thymeleaf Context
            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            if (model != null) {
                model.forEach(context::setVariable);
            }

            if (templateName.contains("::")) {
                String[] parts = templateName.split("::", 2);
                String baseTemplate = parts[0].trim();
                String fragment = parts[1].trim();
                return templateEngine.process(baseTemplate, Collections.singleton(fragment), context);
            }

            return templateEngine.process(templateName, context);
        } catch (Exception e) {
            System.err.println("Template rendering error for: " + templateName);
            e.printStackTrace();
            return createErrorPage(templateName, e);
        }
    }

    // Simple method overload for templates without model
    public String render(String templateName) {
        return render(templateName, new HashMap<>());
    }

    private String createErrorPage(String templateName, Exception e) {
        return String.format(
                "<html><head><title>Template Error</title></head>" + "<body><h1>Template Error</h1>"
                        + "<p><strong>Template:</strong> %s</p>" + "<p><strong>Error:</strong> %s</p>"
                        + "<p><strong>Message:</strong> %s</p>" + "</body></html>",
                templateName, e.getClass().getSimpleName(), e.getMessage());
    }
}

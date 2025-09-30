package org.iu.handelspartnern.spark.util;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import spark.Request;
import spark.Response;

/**
 * Error handling utilities for Spark Java application
 */
public class ErrorUtils {

    public static String handleException(Exception e, TemplateEngine templateEngine, Request request,
            Response response) {
        System.err.println("Error occurred: " + e.getMessage());
        e.printStackTrace();

        WebContext context = ThymeleafContextUtils.createWebContext(request, response);
        context.setVariable("error", e.getMessage());
        context.setVariable("stackTrace", getStackTrace(e));

        try {
            return templateEngine.process("error", context);
        } catch (Exception templateError) {
            // Fallback to simple HTML error page
            return "<html><body><h1>Error</h1><p>" + e.getMessage() + "</p></body></html>";
        }
    }

    private static String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
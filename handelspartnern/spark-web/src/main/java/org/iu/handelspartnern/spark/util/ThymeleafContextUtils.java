package org.iu.handelspartnern.spark.util;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.thymeleaf.context.WebContext;

import spark.Request;
import spark.Response;

/**
 * Utility helpers for creating Thymeleaf {@link WebContext} instances within
 * the Spark Java environment.
 */
public final class ThymeleafContextUtils {

    private static final AtomicReference<ServletContext> CACHED_SERVLET_CONTEXT = new AtomicReference<>();

    private ThymeleafContextUtils() {
    }

    /**
     * Creates a {@link WebContext} based on the current Spark request/response.
     *
     * @param request  the Spark request
     * @param response the Spark response
     * @return the initialized web context
     */
    public static WebContext createWebContext(Request request, Response response) {
        return createWebContext(request, response, null);
    }

    /**
     * Creates a {@link WebContext} based on the current Spark request/response and
     * populates it with
     * the provided model attributes.
     *
     * @param request   the Spark request
     * @param response  the Spark response
     * @param variables optional model attributes to pre-populate the context with
     * @return the initialized web context
     */
    public static WebContext createWebContext(Request request, Response response, Map<String, Object> variables) {
        HttpServletRequest servletRequest = request.raw();
        HttpServletResponse servletResponse = response.raw();
        ServletContext servletContext = resolveServletContext(servletRequest);
        Locale locale = servletRequest.getLocale() != null ? servletRequest.getLocale() : Locale.getDefault();

        WebContext context = new WebContext(servletRequest, servletResponse, servletContext, locale);
        if (variables != null) {
            variables.forEach(context::setVariable);
        }
        return context;
    }

    private static ServletContext resolveServletContext(HttpServletRequest servletRequest) {
        ServletContext context = servletRequest.getServletContext();
        if (context != null) {
            CACHED_SERVLET_CONTEXT.compareAndSet(null, context);
            return context;
        }

        ServletContext cached = CACHED_SERVLET_CONTEXT.get();
        if (cached != null) {
            return cached;
        }

        ServletContext fallback = createFallbackServletContext();
        if (CACHED_SERVLET_CONTEXT.compareAndSet(null, fallback)) {
            return fallback;
        }
        return CACHED_SERVLET_CONTEXT.get();
    }

    private static ServletContext createFallbackServletContext() {
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        return handler.getServletContext();
    }
}

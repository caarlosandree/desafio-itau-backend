package com.itau.aplicacao.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filtro que define um loggerId único por requisição no MDC para rastreabilidade nos logs.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcLoggingFilter extends OncePerRequestFilter {

    public static final String MDC_LOGGER_ID = "loggerId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String loggerId = request.getHeader("X-Request-Id");
        if (loggerId == null || loggerId.isBlank()) {
            loggerId = UUID.randomUUID().toString().substring(0, 8);
        }
        MDC.put(MDC_LOGGER_ID, loggerId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_LOGGER_ID);
        }
    }
}

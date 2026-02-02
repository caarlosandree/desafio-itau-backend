package com.itau.aplicacao.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que registra em log o endpoint chamado (método + path) e o resultado (status e sucesso/erro).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        filterChain.doFilter(request, response);

        String method = request.getMethod();
        String path = request.getRequestURI();
        int status = response.getStatus();
        String resultado = status >= 200 && status < 300 ? "sucesso" : "erro";

        if (status >= 400) {
            log.warn("{} {} -> {} ({})", method, path, status, resultado);
        } else {
            log.info("{} {} -> {} ({})", method, path, status, resultado);
        }
    }
}

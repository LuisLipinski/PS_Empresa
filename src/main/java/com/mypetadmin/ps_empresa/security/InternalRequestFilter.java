package com.mypetadmin.ps_empresa.security;

import com.mypetadmin.ps_empresa.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
@Slf4j
public class InternalRequestFilter extends OncePerRequestFilter {

    private final String internalKey;
    private final ObjectMapper objectMapper;

    public InternalRequestFilter(@Value("${security.internal.key}") String internalKey,
                                 ObjectMapper objectMapper) {
        this.internalKey = internalKey;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/internal/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String providedKey = request.getHeader("X-Internal-Key");

        if (!isValid(providedKey)) {
            log.warn("internal.request denied method={} path={}", request.getMethod(), request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(
                    response.getOutputStream(),
                    ErrorResponse.of(
                            "INVALID_INTERNAL_CREDENTIAL",
                            "Credencial interna ausente ou inválida.",
                            HttpServletResponse.SC_FORBIDDEN,
                            request.getRequestURI()
                    )
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValid(String providedKey) {
        if (providedKey == null || internalKey == null) {
            return false;
        }
        return MessageDigest.isEqual(
                internalKey.getBytes(StandardCharsets.UTF_8),
                providedKey.getBytes(StandardCharsets.UTF_8)
        );
    }
}

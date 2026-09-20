package com.mypetadmin.ps_empresa.security;

import com.mypetadmin.ps_empresa.exception.ErrorResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final String jwtSecret;
    private final String internalKey;

    public JwtAuthenticationFilter(ObjectMapper objectMapper,
                                   @Value("${security.jwt.secret}") String jwtSecret,
                                   @Value("${security.internal.key}") String internalKey) {
        this.objectMapper = objectMapper;
        this.jwtSecret = jwtSecret;
        this.internalKey = internalKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/internal/")
                || path.equals("/actuator/health")
                || path.startsWith("/actuator/health/")
                || path.equals("/actuator/info")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")
                || path.equals("/version");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (hasValidInternalCredential(request)) {
            var authentication = new UsernamePasswordAuthenticationToken(
                    "internal-service",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            writeUnauthorized(response, request, "Token de autenticação ausente ou inválido.");
            return;
        }

        String token = authorization.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            UUID userId = parseRequiredUuid(claims.getSubject(), "subject");
            UUID empresaId = parseRequiredUuid(claims.get("empresaId", String.class), "empresaId");

            List<?> roles = claims.get("roles", List.class);
            var authorities = roles == null
                    ? Collections.<SimpleGrantedAuthority>emptyList()
                    : roles.stream()
                    .map(String::valueOf)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            var principal = new JwtTenantPrincipal(userId, empresaId);
            var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            writeUnauthorized(response, request, "Token de autenticação expirado, inválido ou sem contexto de tenant.");
        }
    }

    private UUID parseRequiredUuid(String rawValue, String claimName) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("Claim obrigatório ausente: " + claimName);
        }
        return UUID.fromString(rawValue);
    }

    private boolean hasValidInternalCredential(HttpServletRequest request) {
        String providedKey = request.getHeader("X-Internal-Key");
        if (providedKey == null || internalKey == null) {
            return false;
        }
        return MessageDigest.isEqual(
                internalKey.getBytes(StandardCharsets.UTF_8),
                providedKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    private void writeUnauthorized(HttpServletResponse response,
                                   HttpServletRequest request,
                                   String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getOutputStream(),
                ErrorResponse.of("AUTHENTICATION_REQUIRED", message, HttpServletResponse.SC_UNAUTHORIZED, request.getRequestURI())
        );
    }
}

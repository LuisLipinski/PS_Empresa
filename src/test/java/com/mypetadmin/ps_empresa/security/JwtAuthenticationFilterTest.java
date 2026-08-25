package com.mypetadmin.ps_empresa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationFilterTest {

    private static final String JWT_SECRET = "mypetadmin-test-jwt-secret-key-32chars";
    private static final String INTERNAL_KEY = "test-internal-key";
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
            new ObjectMapper().findAndRegisterModules(), JWT_SECRET, INTERNAL_KEY
    );

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveAutenticarTokenValido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/empresas");
        request.addHeader("Authorization", "Bearer " + tokenValido());
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> ((MockHttpServletResponse) res).setStatus(200);
        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("usuario@teste.com");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting(Object::toString)
                .contains("ROLE_ADMIN");
    }

    @Test
    void deveAutenticarServicoComChaveInterna() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/empresas");
        request.addHeader("X-Internal-Key", INTERNAL_KEY);
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> ((MockHttpServletResponse) res).setStatus(200);
        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("internal-service");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting(Object::toString)
                .contains("ROLE_INTERNAL");
    }

    @Test
    void deveRetornar401SemCredencial() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/empresas");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {});

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("AUTHENTICATION_REQUIRED");
    }

    @Test
    void deveRetornar401ParaTokenInvalido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/empresas");
        request.addHeader("Authorization", "Bearer token-invalido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {});

        assertThat(response.getStatus()).isEqualTo(401);
    }

    private String tokenValido() {
        return Jwts.builder()
                .setSubject("usuario@teste.com")
                .claim("roles", List.of("ROLE_ADMIN"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }
}

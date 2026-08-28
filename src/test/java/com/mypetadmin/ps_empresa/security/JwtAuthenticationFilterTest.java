package com.mypetadmin.ps_empresa.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationFilterTest {

    private static final String JWT_SECRET = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
    private static final String INTERNAL_KEY = "test-internal-key";
    private static final UUID USER_ID = UUID.fromString("11111111-1111-4111-8111-111111111111");
    private static final UUID EMPRESA_ID = UUID.fromString("22222222-2222-4222-8222-222222222222");

    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
            new ObjectMapper(), JWT_SECRET, INTERNAL_KEY
    );

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveAutenticarTokenValidoComContextoDeTenant() throws Exception {
        MockHttpServletResponse response = executarComToken(tokenValido());

        assertThat(response.getStatus()).isEqualTo(200);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication.getPrincipal()).isEqualTo(new JwtTenantPrincipal(USER_ID, EMPRESA_ID));
        assertThat(authentication.getName()).isEqualTo(USER_ID.toString());
        assertThat(authentication.getAuthorities())
                .extracting(Object::toString)
                .contains("MASTER");
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
        MockHttpServletResponse response = executarComToken("token-invalido");
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void deveRetornar401QuandoEmpresaIdEstiverAusente() throws Exception {
        String token = Jwts.builder()
                .setSubject(USER_ID.toString())
                .claim("roles", List.of("MASTER"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();

        MockHttpServletResponse response = executarComToken(token);
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void deveRetornar401QuandoSubjectNaoForUuid() throws Exception {
        String token = Jwts.builder()
                .setSubject("usuario@teste.com")
                .claim("empresaId", EMPRESA_ID.toString())
                .claim("roles", List.of("MASTER"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();

        MockHttpServletResponse response = executarComToken(token);
        assertThat(response.getStatus()).isEqualTo(401);
    }

    private MockHttpServletResponse executarComToken(String token) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/empresas/me");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> ((MockHttpServletResponse) res).setStatus(200);
        filter.doFilter(request, response, chain);
        return response;
    }

    private String tokenValido() {
        return Jwts.builder()
                .setSubject(USER_ID.toString())
                .claim("empresaId", EMPRESA_ID.toString())
                .claim("roles", List.of("MASTER"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }
}

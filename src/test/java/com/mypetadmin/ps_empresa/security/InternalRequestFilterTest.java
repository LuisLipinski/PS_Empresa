package com.mypetadmin.ps_empresa.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class InternalRequestFilterTest {

    private final InternalRequestFilter filter = new InternalRequestFilter("test-internal-key", new ObjectMapper());

    @Test
    void devePermitirRequisicaoComHeaderCorreto() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/internal/contratos/status");
        request.addHeader("X-Internal-Key", "test-internal-key");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> ((MockHttpServletResponse) res).setStatus(204);
        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    void deveBloquearQuandoHeaderIncorreto() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/internal/contratos/status");
        request.addHeader("X-Internal-Key", "errado");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {});

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("INVALID_INTERNAL_CREDENTIAL");
    }

    @Test
    void deveBloquearQuandoHeaderAusente() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/internal/empresas");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {});

        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void deveIgnorarRotasNaoInternas() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/empresas");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> ((MockHttpServletResponse) res).setStatus(200);
        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
    }
}

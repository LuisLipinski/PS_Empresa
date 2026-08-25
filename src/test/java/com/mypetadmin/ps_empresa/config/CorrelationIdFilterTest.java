package com.mypetadmin.ps_empresa.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void devePreservarCorrelationIdRecebidoDuranteARequisicao() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        AtomicReference<String> correlationInChain = new AtomicReference<>();

        when(request.getHeader(CorrelationIdFilter.HEADER_NAME)).thenReturn("corr-123");
        doAnswer(invocation -> {
            correlationInChain.set(MDC.get("correlationId"));
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        assertEquals("corr-123", correlationInChain.get());
        verify(response).setHeader(CorrelationIdFilter.HEADER_NAME, "corr-123");
        assertNull(MDC.get("correlationId"));
    }

    @Test
    void deveGerarCorrelationIdQuandoHeaderNaoFoiEnviado() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        AtomicReference<String> correlationInChain = new AtomicReference<>();

        when(request.getHeader(CorrelationIdFilter.HEADER_NAME)).thenReturn(null);
        doAnswer(invocation -> {
            correlationInChain.set(MDC.get("correlationId"));
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        assertNotNull(correlationInChain.get());
        verify(response).setHeader(CorrelationIdFilter.HEADER_NAME, correlationInChain.get());
        assertNull(MDC.get("correlationId"));
    }
}

package com.mypetadmin.ps_empresa.service.impl;

import com.mypetadmin.ps_empresa.dto.EmpresaContratoStatusDTO;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.exception.EmpresaNaoEncontradaException;
import com.mypetadmin.ps_empresa.exception.StatusInvalidException;
import com.mypetadmin.ps_empresa.model.Empresa;
import com.mypetadmin.ps_empresa.repository.EmpresaRepository;
import com.mypetadmin.ps_empresa.mapper.EmpresaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceImplSincronizarStatusTest {

    @InjectMocks
    private EmpresaServiceImpl service;

    @Mock
    private EmpresaRepository repository;

    @Mock
    private EmpresaMapper mapper;

    private Empresa empresa;
    private UUID empresaId;

    @BeforeEach
    void setup() {
        empresaId = UUID.randomUUID();
        empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setStatus(StatusEmpresa.AGUARDANDO_CONTRATO);
    }

    @Test
    void deveAtualizarParaAtivo() {
        EmpresaContratoStatusDTO dto = new EmpresaContratoStatusDTO();
        dto.setEmpresaId(empresaId);
        dto.setStatusContrato("ATIVO");

        when(repository.findById(empresaId)).thenReturn(Optional.of(empresa));

        service.sincronizarStatusComContrato(dto);

        assertThat(empresa.getStatus()).isEqualTo(StatusEmpresa.ATIVO);
        verify(repository).save(empresa);
    }

    @Test
    void deveAtualizarParaAguardandoContratoQuandoReceberAguardandoPagamento() {
        EmpresaContratoStatusDTO dto = new EmpresaContratoStatusDTO();
        dto.setEmpresaId(empresaId);
        dto.setStatusContrato("AGUARDANDO_PAGAMENTO");

        when(repository.findById(empresaId)).thenReturn(Optional.of(empresa));

        service.sincronizarStatusComContrato(dto);

        assertThat(empresa.getStatus()).isEqualTo(StatusEmpresa.AGUARDANDO_CONTRATO);
        verify(repository).save(empresa);
    }

    @Test
    void deveManterAtivoQuandoReceberPendentePagamentoEEmpresaJaEstiverAtiva() {
        empresa.setStatus(StatusEmpresa.ATIVO);

        EmpresaContratoStatusDTO dto = new EmpresaContratoStatusDTO();
        dto.setEmpresaId(empresaId);
        dto.setStatusContrato("PENDENTE_PAGAMENTO");

        when(repository.findById(empresaId)).thenReturn(Optional.of(empresa));

        service.sincronizarStatusComContrato(dto);

        assertThat(empresa.getStatus()).isEqualTo(StatusEmpresa.ATIVO);
        verify(repository).save(empresa);
    }

    @Test
    void deveAtualizarParaInativo() {
        EmpresaContratoStatusDTO dto = new EmpresaContratoStatusDTO();
        dto.setEmpresaId(empresaId);
        dto.setStatusContrato("INATIVO");

        when(repository.findById(empresaId)).thenReturn(Optional.of(empresa));

        service.sincronizarStatusComContrato(dto);

        assertThat(empresa.getStatus()).isEqualTo(StatusEmpresa.INATIVO);
        verify(repository).save(empresa);
    }

    @Test
    void deveLancarExcecaoQuandoEmpresaNaoExiste() {
        EmpresaContratoStatusDTO dto = new EmpresaContratoStatusDTO();
        dto.setEmpresaId(empresaId);

        when(repository.findById(empresaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.sincronizarStatusComContrato(dto))
                .isInstanceOf(EmpresaNaoEncontradaException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoStatusContratoForInvalido() {
        EmpresaContratoStatusDTO dto = new EmpresaContratoStatusDTO();
        dto.setEmpresaId(empresaId);
        dto.setStatusContrato("STATUS_QUALQUER");

        when(repository.findById(empresaId)).thenReturn(Optional.of(empresa));

        assertThatThrownBy(() -> service.sincronizarStatusComContrato(dto))
                .isInstanceOf(StatusInvalidException.class);

        verify(repository, never()).save(any());
    }
}
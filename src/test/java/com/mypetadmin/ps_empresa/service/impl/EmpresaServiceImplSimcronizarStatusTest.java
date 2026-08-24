package com.mypetadmin.ps_empresa.service.impl;

import com.mypetadmin.ps_empresa.dto.EmpresaContratoStatusDTO;
import com.mypetadmin.ps_empresa.enums.StatusContrato;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.exception.EmpresaNaoEncontradaException;
import com.mypetadmin.ps_empresa.mapper.EmpresaMapper;
import com.mypetadmin.ps_empresa.model.Empresa;
import com.mypetadmin.ps_empresa.repository.EmpresaRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceImplSimcronizarStatusTest {

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
        EmpresaContratoStatusDTO dto = dto(StatusContrato.ATIVO);
        when(repository.findById(empresaId)).thenReturn(Optional.of(empresa));

        service.sincronizarStatusComContrato(dto);

        assertThat(empresa.getStatus()).isEqualTo(StatusEmpresa.ATIVO);
        assertThat(empresa.getDataAtualizacaoStatus()).isNotNull();
        verify(repository).save(empresa);
    }

    @Test
    void deveAtualizarParaAguardandoContrato() {
        empresa.setStatus(StatusEmpresa.ATIVO);
        EmpresaContratoStatusDTO dto = dto(StatusContrato.AGUARDANDO_PAGAMENTO);
        when(repository.findById(empresaId)).thenReturn(Optional.of(empresa));

        service.sincronizarStatusComContrato(dto);

        assertThat(empresa.getStatus()).isEqualTo(StatusEmpresa.AGUARDANDO_CONTRATO);
    }

    @Test
    void deveManterAtivoQuandoPendentePagamentoEJaEstiverAtivo() {
        empresa.setStatus(StatusEmpresa.ATIVO);
        EmpresaContratoStatusDTO dto = dto(StatusContrato.PENDENTE_PAGAMENTO);
        when(repository.findById(empresaId)).thenReturn(Optional.of(empresa));

        service.sincronizarStatusComContrato(dto);

        assertThat(empresa.getStatus()).isEqualTo(StatusEmpresa.ATIVO);
    }

    @Test
    void naoDeveAtivarQuandoPendentePagamentoEEmpresaAindaAguardandoContrato() {
        EmpresaContratoStatusDTO dto = dto(StatusContrato.PENDENTE_PAGAMENTO);
        when(repository.findById(empresaId)).thenReturn(Optional.of(empresa));

        service.sincronizarStatusComContrato(dto);

        assertThat(empresa.getStatus()).isEqualTo(StatusEmpresa.AGUARDANDO_CONTRATO);
    }

    @Test
    void deveAtualizarParaInativo() {
        EmpresaContratoStatusDTO dto = dto(StatusContrato.INATIVO);
        when(repository.findById(empresaId)).thenReturn(Optional.of(empresa));

        service.sincronizarStatusComContrato(dto);

        assertThat(empresa.getStatus()).isEqualTo(StatusEmpresa.INATIVO);
    }

    @Test
    void deveLancarExcecaoQuandoEmpresaNaoExiste() {
        EmpresaContratoStatusDTO dto = dto(StatusContrato.ATIVO);
        when(repository.findById(empresaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.sincronizarStatusComContrato(dto))
                .isInstanceOf(EmpresaNaoEncontradaException.class);
    }

    private EmpresaContratoStatusDTO dto(StatusContrato status) {
        EmpresaContratoStatusDTO dto = new EmpresaContratoStatusDTO();
        dto.setEmpresaId(empresaId);
        dto.setStatusContrato(status);
        return dto;
    }
}

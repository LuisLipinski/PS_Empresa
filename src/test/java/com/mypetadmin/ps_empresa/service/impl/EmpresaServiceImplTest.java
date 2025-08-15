package com.mypetadmin.ps_empresa.service.impl;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.exception.EmailExistenteException;
import com.mypetadmin.ps_empresa.exception.EmpresaExistenteException;
import com.mypetadmin.ps_empresa.exception.EmpresaNaoEncontradaException;
import com.mypetadmin.ps_empresa.mapper.EmpresaMapper;
import com.mypetadmin.ps_empresa.model.Empresa;
import com.mypetadmin.ps_empresa.repository.EmpresaRepository;
import com.mypetadmin.ps_empresa.util.CnpjValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceImplTest {

    @InjectMocks
    private EmpresaServiceImpl empresaService;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EmpresaMapper mapper;

    private EmpresaRequestDTO dto;
    private Empresa entity;
    private EmpresaResponseDTO response;

    @BeforeEach
    void setUp() {
        dto = new EmpresaRequestDTO();
        dto.setDocumentNumber("34222351000169");
        dto.setEmail("empresa@teste.com");

        entity = new Empresa();
        entity.setId(UUID.randomUUID());
        entity.setStatus(StatusEmpresa.AGUARDANDO_PAGAMENTO);

        response = new EmpresaResponseDTO();
        response.setId(entity.getId());
    }

    @Test
    void deveCadastrarEmpresaComSucesso() {
        when(empresaRepository.existsByDocumentNumber(dto.getDocumentNumber())).thenReturn(false);
        when(empresaRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(empresaRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponseDto(entity)).thenReturn(response);

        EmpresaResponseDTO resultado = empresaService.cadastrarEmpresa(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(entity.getId());
        verify(empresaRepository).save(entity);
    }

    @Test
    void deveLancarExcecaoCnpjExistente() {
        EmpresaRequestDTO dto = new EmpresaRequestDTO();
        dto.setDocumentNumber("34222351000169");

        when(empresaRepository.existsByDocumentNumber(dto.getDocumentNumber())).thenReturn(true);

        assertThatThrownBy(() -> empresaService.cadastrarEmpresa(dto))
                .isInstanceOf(EmpresaExistenteException.class)
                .hasMessageContaining("CNPJ já cadastrado");
    }

    @Test
    void deveLancarExcecaoEmailExistente() {
        EmpresaRequestDTO dto = new EmpresaRequestDTO();
        dto.setDocumentNumber("86054433000145");
        dto.setEmail("empresa@teste.com");

        when(empresaRepository.existsByDocumentNumber(dto.getDocumentNumber())).thenReturn(false);
        when(empresaRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        try (MockedStatic<CnpjValidator> mocked = Mockito.mockStatic(CnpjValidator.class)) {
            mocked.when(() -> CnpjValidator.isCnpjValid(dto.getDocumentNumber())).thenReturn(true);

            assertThatThrownBy(() -> empresaService.cadastrarEmpresa(dto))
                    .isInstanceOf(EmailExistenteException.class)
                    .hasMessageContaining("Email já cadastrado");
        }
    }

    @Test
    void deveLancarExcecaoCnpjInvalido() {
        EmpresaRequestDTO dto = new EmpresaRequestDTO();
        dto.setDocumentNumber("00000000000000");
        dto.setEmail("teste@email.com");

        when(empresaRepository.existsByDocumentNumber(dto.getDocumentNumber())).thenReturn(false);

        try (MockedStatic<CnpjValidator> mocked = Mockito.mockStatic(CnpjValidator.class)) {
            mocked.when(() -> CnpjValidator.isCnpjValid(dto.getDocumentNumber())).thenReturn(false);

            assertThatThrownBy(() -> empresaService.cadastrarEmpresa(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CNPJ inválido");
        }
    }

    @Test
    void atualizarStatus_quandoEmpresaNaoExiste_entaoLancaExcecao() {
        when(empresaRepository.findById(entity.getId())).thenReturn(Optional.empty());

        EmpresaNaoEncontradaException exception = assertThrows(
                EmpresaNaoEncontradaException.class,
                () -> empresaService.atualizarStatus(entity.getId(), StatusEmpresa.ATIVO)
        );

        assertEquals("Empresa não encontrada", exception.getMessage());
        verify(empresaRepository,never()).save(any());
    }

    @Test
    void atualizarStatus_quandoNovoStatusDiferente_entaoAtualizaStatus() {
        when(empresaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        empresaService.atualizarStatus(entity.getId(), StatusEmpresa.ATIVO);

        assertEquals(StatusEmpresa.ATIVO, entity.getStatus());
        assertNotNull(entity.getDataAtualizacaoStatus());
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void atualizarStatus_quandoNovoStatusIgual_entaoNaoAtualiza() {
        when(empresaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        empresaService.atualizarStatus(entity.getId(), StatusEmpresa.AGUARDANDO_PAGAMENTO);

        assertEquals(StatusEmpresa.AGUARDANDO_PAGAMENTO, entity.getStatus());
        assertNull(entity.getDataAtualizacaoStatus());
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void atualizarStatus_quandoStatusNulo_entaoLancaNullPointerException() {

        assertThrows(NullPointerException.class,
                () -> empresaService.atualizarStatus(entity.getId(), null));
    }

}

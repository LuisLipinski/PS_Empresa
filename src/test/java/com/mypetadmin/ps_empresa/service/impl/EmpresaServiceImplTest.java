package com.mypetadmin.ps_empresa.service.impl;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.enums.DirectionField;
import com.mypetadmin.ps_empresa.enums.SortField;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.exception.CnpjInvalidException;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
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
                    .isInstanceOf(CnpjInvalidException.class)
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

    @Test
    void getAllEmpresaSorted_comFiltrosValidados_retornaLista() {
        String cnpj = "34222351000169";
        String razaoSocial = "Pet Shop";
        StatusEmpresa status = StatusEmpresa.ATIVO;

        Empresa empresa1 = new Empresa();
        empresa1.setId(UUID.randomUUID());
        Empresa empresa2 = new Empresa();
        empresa2.setId(UUID.randomUUID());

        List<Empresa> empresas = Arrays.asList(empresa1, empresa2);

        try (MockedStatic<CnpjValidator> mocked = Mockito.mockStatic(CnpjValidator.class)) {
            mocked.when(() -> CnpjValidator.isCnpjValid(cnpj)).thenReturn(true);

            when(empresaRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(empresas);
            when(mapper.toResponseDto(any(Empresa.class))).thenReturn(new EmpresaResponseDTO());

            List<EmpresaResponseDTO> resultado = empresaService.getAllEmpresaSorted(
                    cnpj, razaoSocial, null, status, SortField.RAZAO_SOCIAL, DirectionField.ASC
            );

            assertThat(resultado).hasSize(2);
            verify(empresaRepository).findAll(any(Specification.class), any(Sort.class));
            verify(mapper, times(2)).toResponseDto(any());
        }
    }

    @Test
    void getAllEmpresaSorted_cnpjInvalido_lancaExcecao() {
        String cnpj = "00000000000000";

        try (MockedStatic<CnpjValidator> mocked = Mockito.mockStatic(CnpjValidator.class)) {
            mocked.when(() -> CnpjValidator.isCnpjValid(cnpj)).thenReturn(false);

            assertThatThrownBy(() -> empresaService.getAllEmpresaSorted(
                    cnpj, null, null, null, SortField.RAZAO_SOCIAL, DirectionField.ASC
            )).isInstanceOf(CnpjInvalidException.class)
                    .hasMessageContaining("Cnpj informado é invalido");

            verify(empresaRepository, never()).findAll(any(Specification.class), any(Sort.class));
        }
    }

    @Test
    void getAllEmpresaSorted_semFiltros_retornaListaVazia() {
        when(empresaRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(Arrays.asList());

        List<EmpresaResponseDTO> resultado = empresaService.getAllEmpresaSorted(
                null, null, null, null, SortField.RAZAO_SOCIAL, DirectionField.ASC
        );

        assertThat(resultado).isEmpty();
    }

    @Test
    void getEmpresaById_sucesso() {
        UUID id = UUID.randomUUID();
        when(empresaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toResponseDto(entity)).thenReturn(response);

        EmpresaResponseDTO resultado = empresaService.getEmpresaById(id);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(entity.getId());
        verify(empresaRepository).findById(id);
        verify(mapper).toResponseDto(entity);
    }

    @Test
    void getEmpresaById_naoEncontrada_lancaExcecao() {
        UUID id = UUID.randomUUID();
        when(empresaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empresaService.getEmpresaById(id))
                .isInstanceOf(EmpresaNaoEncontradaException.class)
                .hasMessageContaining("Empresa não encontrada");

        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    void deleteEmpresaById_quandoEmpresaExiste_entaoDeletaComSucesso() {

        UUID id = UUID.randomUUID();
        Empresa empresa = new Empresa();
        empresa.setId(id);

        when(empresaRepository.findById(id)).thenReturn(Optional.of(empresa));

        empresaService.deleteEmpresaById(id);

        verify(empresaRepository, times(1)).findById(id);
        verify(empresaRepository, times(1)).delete(empresa);
    }

    @Test
    void deleteEmpresaById_quandoEmpresaNaoExiste_entaoLancaExcecao() {

        UUID id = UUID.randomUUID();
        when(empresaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empresaService.deleteEmpresaById(id))
                .isInstanceOf(EmpresaNaoEncontradaException.class)
                .hasMessageContaining("Empresa não encontrada com o id: " + id);

        verify(empresaRepository, times(1)).findById(id);
        verify(empresaRepository, never()).delete(any(Empresa.class));
    }

}

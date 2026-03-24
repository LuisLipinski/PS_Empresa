package com.mypetadmin.ps_empresa.service.impl;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.dto.PageResponse;
import com.mypetadmin.ps_empresa.dto.UpdateEmpresaRequestDto;
import com.mypetadmin.ps_empresa.enums.DirectionField;
import com.mypetadmin.ps_empresa.enums.SortField;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.exception.CnpjInvalidException;
import com.mypetadmin.ps_empresa.exception.EmpresaExistenteException;
import com.mypetadmin.ps_empresa.exception.EmpresaNaoEncontradaException;
import com.mypetadmin.ps_empresa.mapper.EmpresaMapper;
import com.mypetadmin.ps_empresa.model.Empresa;
import com.mypetadmin.ps_empresa.repository.EmpresaRepository;
import com.mypetadmin.ps_empresa.util.CnpjValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        entity = new Empresa();
        entity.setId(UUID.randomUUID());

        response = new EmpresaResponseDTO();
        response.setId(entity.getId());
    }

    @Test
    void deveCadastrarEmpresaComSucesso() {
        when(empresaRepository.existsByDocumentNumber(dto.getDocumentNumber())).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(empresaRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponseDto(entity)).thenReturn(response);

        EmpresaResponseDTO resultado = empresaService.cadastrarEmpresa(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(entity.getId());

        verify(empresaRepository).existsByDocumentNumber(dto.getDocumentNumber());
        verify(mapper).toEntity(dto);
        verify(empresaRepository).save(entity);
        verify(mapper).toResponseDto(entity);
    }

    @Test
    void deveRetornarStatusAguardandoContratoNoCadastroQuandoMapperJaDefinirStatusInicial() {
        entity.setStatus(StatusEmpresa.AGUARDANDO_CONTRATO);

        when(empresaRepository.existsByDocumentNumber(dto.getDocumentNumber())).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(empresaRepository.save(any(Empresa.class))).thenReturn(entity);
        when(mapper.toResponseDto(any(Empresa.class))).thenAnswer(invocation -> {
            Empresa emp = invocation.getArgument(0);
            EmpresaResponseDTO resp = new EmpresaResponseDTO();
            resp.setStatus(emp.getStatus());
            return resp;
        });

        EmpresaResponseDTO response = empresaService.cadastrarEmpresa(dto);

        assertThat(response.getStatus()).isEqualTo(StatusEmpresa.AGUARDANDO_CONTRATO);
    }

    @Test
    void deveLancarExcecaoQuandoCnpjJaExiste() {
        when(empresaRepository.existsByDocumentNumber(dto.getDocumentNumber())).thenReturn(true);

        assertThatThrownBy(() -> empresaService.cadastrarEmpresa(dto))
                .isInstanceOf(EmpresaExistenteException.class)
                .hasMessageContaining("CNPJ já cadastrado");

        verify(empresaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoCnpjInvalido() {
        dto.setDocumentNumber("00000000000000");

        when(empresaRepository.existsByDocumentNumber(dto.getDocumentNumber())).thenReturn(false);

        try (MockedStatic<CnpjValidator> mocked = Mockito.mockStatic(CnpjValidator.class)) {
            mocked.when(() -> CnpjValidator.isCnpjValid(dto.getDocumentNumber())).thenReturn(false);

            assertThatThrownBy(() -> empresaService.cadastrarEmpresa(dto))
                    .isInstanceOf(CnpjInvalidException.class)
                    .hasMessageContaining("CNPJ inválido");

            verify(empresaRepository, never()).save(any());
        }
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

        Page<Empresa> page = new PageImpl<>(
                Arrays.asList(empresa1, empresa2),
                PageRequest.of(0, 10),
                2
        );

        try (MockedStatic<CnpjValidator> mocked = Mockito.mockStatic(CnpjValidator.class)) {
            mocked.when(() -> CnpjValidator.isCnpjValid(cnpj)).thenReturn(true);

            when(empresaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            when(mapper.toResponseDto(any(Empresa.class))).thenReturn(new EmpresaResponseDTO());

            PageResponse<EmpresaResponseDTO> resultado = empresaService.getAllEmpresaSorted(
                    cnpj, razaoSocial, status, 0, 10, SortField.RAZAO_SOCIAL, DirectionField.ASC
            );

            assertThat(resultado.getContent()).hasSize(2);
            assertThat(resultado.getTotalElements()).isEqualTo(2);
            assertThat(resultado.getTotalPages()).isEqualTo(1);

            verify(empresaRepository).findAll(any(Specification.class), any(Pageable.class));
            verify(mapper, times(2)).toResponseDto(any(Empresa.class));
        }
    }

    @Test
    void getAllEmpresaSorted_quandoCnpjInvalido_lancaExcecaoESemConsultarRepositorio() {
        String cnpj = "00000000000000";

        try (MockedStatic<CnpjValidator> mocked = Mockito.mockStatic(CnpjValidator.class)) {
            mocked.when(() -> CnpjValidator.isCnpjValid(cnpj)).thenReturn(false);

            assertThatThrownBy(() -> empresaService.getAllEmpresaSorted(
                    cnpj, null, null, 0, 10, SortField.RAZAO_SOCIAL, DirectionField.ASC
            ))
                    .isInstanceOf(CnpjInvalidException.class)
                    .hasMessageContaining("Cnpj informado é invalido");

            verify(empresaRepository, never()).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    void getAllEmpresaSorted_semFiltros_retornaListaVazia() {
        when(empresaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        PageResponse<EmpresaResponseDTO> resultado = empresaService.getAllEmpresaSorted(
                null, null, null, 0, 10, SortField.RAZAO_SOCIAL, DirectionField.ASC
        );

        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
        assertThat(resultado.getTotalPages()).isZero();
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

        verify(empresaRepository).findById(id);
        verify(empresaRepository).delete(empresa);
    }

    @Test
    void deleteEmpresaById_quandoEmpresaNaoExiste_entaoLancaExcecao() {
        UUID id = UUID.randomUUID();

        when(empresaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empresaService.deleteEmpresaById(id))
                .isInstanceOf(EmpresaNaoEncontradaException.class)
                .hasMessageContaining("Empresa não encontrada com o id: " + id);

        verify(empresaRepository).findById(id);
        verify(empresaRepository, never()).delete(any(Empresa.class));
    }

    @Test
    void editEmpresaById_quandoEmpresaExiste_entaoAtualizaComSucesso() {
        UUID empresaId = entity.getId();

        UpdateEmpresaRequestDto updateDto = new UpdateEmpresaRequestDto();
        updateDto.setNomeFantasia("Novo Nome");

        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(entity));
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponseDto(any(Empresa.class))).thenReturn(response);

        EmpresaResponseDTO resultado = empresaService.editEmpresaById(empresaId, updateDto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(entity.getId());

        verify(empresaRepository).findById(empresaId);
        verify(empresaRepository).save(entity);
        verify(mapper).toResponseDto(entity);
    }

    @Test
    void editEmpresaById_quandoEmpresaNaoExiste_entaoLancaExcecao() {
        UUID empresaId = UUID.randomUUID();
        UpdateEmpresaRequestDto updateDto = new UpdateEmpresaRequestDto();

        when(empresaRepository.findById(empresaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empresaService.editEmpresaById(empresaId, updateDto))
                .isInstanceOf(EmpresaNaoEncontradaException.class)
                .hasMessageContaining("Empresa não encontrada com o id: " + empresaId);

        verify(empresaRepository).findById(empresaId);
        verify(empresaRepository, never()).save(any());
        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    void editEmpresaById_quandoFalhaAoSalvar_entaoPropagaExcecao() {
        UUID empresaId = entity.getId();
        UpdateEmpresaRequestDto updateDto = new UpdateEmpresaRequestDto();

        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(entity));
        when(empresaRepository.save(any(Empresa.class))).thenThrow(new RuntimeException("Erro ao salvar"));

        assertThatThrownBy(() -> empresaService.editEmpresaById(empresaId, updateDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao salvar");

        verify(empresaRepository).findById(empresaId);
        verify(empresaRepository).save(entity);
        verify(mapper, never()).toResponseDto(any());
    }
}
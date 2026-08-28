package com.mypetadmin.ps_empresa.service.impl;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.dto.PageResponse;
import com.mypetadmin.ps_empresa.dto.UpdateEmpresaRequestDto;
import com.mypetadmin.ps_empresa.enums.DirectionField;
import com.mypetadmin.ps_empresa.enums.SortField;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.exception.CnpjInvalidException;
import com.mypetadmin.ps_empresa.exception.EmailExistenteException;
import com.mypetadmin.ps_empresa.exception.EmpresaExistenteException;
import com.mypetadmin.ps_empresa.exception.EmpresaNaoEncontradaException;
import com.mypetadmin.ps_empresa.exception.OnboardingConflictException;
import com.mypetadmin.ps_empresa.mapper.EmpresaMapper;
import com.mypetadmin.ps_empresa.model.Empresa;
import com.mypetadmin.ps_empresa.repository.EmpresaRepository;
import com.mypetadmin.ps_empresa.util.CnpjValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceImplTest {

    @org.mockito.Mock
    private EmpresaRepository empresaRepository;

    @org.mockito.Mock
    private EmpresaMapper mapper;

    private EmpresaServiceImpl empresaService;
    private EmpresaRequestDTO request;
    private Empresa entity;
    private EmpresaResponseDTO response;

    @BeforeEach
    void setUp() {
        empresaService = new EmpresaServiceImpl(empresaRepository, mapper);

        request = new EmpresaRequestDTO();
        request.setDocumentNumber("34222351000169");
        request.setRazaoSocial("Empresa Teste LTDA");
        request.setNomeFantasia("Empresa Teste");
        request.setTelefone("41999999999");
        request.setEmail("empresa@teste.com");
        request.setNomeTitular("Titular Teste");
        request.setRua("Rua Teste");
        request.setNumero("10");
        request.setComplemento("Casa");
        request.setBairro("Centro");
        request.setCidade("Curitiba");
        request.setEstado("PR");
        request.setCep("01001000");

        entity = new Empresa();
        entity.setId(UUID.randomUUID());
        entity.setEmail("empresa@teste.com");
        entity.setEndereco("Rua Teste, 10, Centro");
        entity.setStatus(StatusEmpresa.AGUARDANDO_CONTRATO);

        response = new EmpresaResponseDTO();
        response.setId(entity.getId());
        response.setStatus(StatusEmpresa.AGUARDANDO_CONTRATO);
    }

    @Test
    void deveCadastrarEmpresaComStatusAguardandoContrato() {
        prepararNovoCadastro();

        try (MockedStatic<CnpjValidator> cnpj = cnpjValido()) {
            EmpresaResponseDTO result = empresaService.cadastrarEmpresa(request);

            assertThat(result.getId()).isEqualTo(entity.getId());
            assertThat(entity.getStatus()).isEqualTo(StatusEmpresa.AGUARDANDO_CONTRATO);
            assertThat(entity.getDataAtualizacaoStatus()).isNotNull();
            assertThat(entity.getOnboardingId()).isNull();
            verify(empresaRepository).save(entity);
        }
    }

    @Test
    void onboardingDeveCriarERepetirIdempotentemente() {
        UUID onboardingId = UUID.randomUUID();
        when(empresaRepository.findByOnboardingId(onboardingId))
                .thenReturn(Optional.empty(), Optional.of(entity));
        prepararNovoCadastro();

        try (MockedStatic<CnpjValidator> cnpj = cnpjValido()) {
            EmpresaResponseDTO first = empresaService.cadastrarEmpresaOnboarding(request, onboardingId);
            EmpresaResponseDTO replay = empresaService.cadastrarEmpresaOnboarding(request, onboardingId);

            assertThat(first).isEqualTo(response);
            assertThat(replay).isEqualTo(response);
            assertThat(entity.getOnboardingId()).isEqualTo(onboardingId);
            assertThat(entity.getOnboardingRequestHash()).hasSize(64);
            verify(empresaRepository, times(2)).lockOnboarding(onboardingId);
            verify(empresaRepository, times(1)).save(entity);
        }
    }

    @Test
    void onboardingDeveRejeitarMesmaChaveComPayloadDiferente() {
        UUID onboardingId = UUID.randomUUID();
        when(empresaRepository.findByOnboardingId(onboardingId))
                .thenReturn(Optional.empty(), Optional.of(entity));
        prepararNovoCadastro();

        try (MockedStatic<CnpjValidator> cnpj = cnpjValido()) {
            empresaService.cadastrarEmpresaOnboarding(request, onboardingId);
            request.setNomeFantasia("Outro Nome");

            assertThatThrownBy(() -> empresaService.cadastrarEmpresaOnboarding(request, onboardingId))
                    .isInstanceOf(OnboardingConflictException.class)
                    .hasMessageContaining("dados diferentes");
        }

        verify(empresaRepository, times(1)).save(entity);
    }

    @Test
    void onboardingDeveExigirId() {
        assertThatThrownBy(() -> empresaService.cadastrarEmpresaOnboarding(request, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("onboardingId");
        verify(empresaRepository, never()).lockOnboarding(any());
    }

    @Test
    void deveRejeitarCnpjJaCadastradoAntesDeOutrasValidacoes() {
        when(empresaRepository.existsByDocumentNumber(request.getDocumentNumber())).thenReturn(true);

        assertThatThrownBy(() -> empresaService.cadastrarEmpresa(request))
                .isInstanceOf(EmpresaExistenteException.class)
                .hasMessageContaining("CNPJ já cadastrado");

        verify(empresaRepository, never()).existsByEmail(any());
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void deveRejeitarCnpjInvalido() {
        when(empresaRepository.existsByDocumentNumber(request.getDocumentNumber())).thenReturn(false);

        try (MockedStatic<CnpjValidator> cnpj = Mockito.mockStatic(CnpjValidator.class)) {
            cnpj.when(() -> CnpjValidator.isCnpjValid(request.getDocumentNumber())).thenReturn(false);

            assertThatThrownBy(() -> empresaService.cadastrarEmpresa(request))
                    .isInstanceOf(CnpjInvalidException.class)
                    .hasMessage("CNPJ inválido.");
        }

        verify(empresaRepository, never()).existsByEmail(any());
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void deveRejeitarEmailJaCadastrado() {
        when(empresaRepository.existsByDocumentNumber(request.getDocumentNumber())).thenReturn(false);
        when(empresaRepository.existsByEmail(request.getEmail())).thenReturn(true);

        try (MockedStatic<CnpjValidator> cnpj = cnpjValido()) {
            assertThatThrownBy(() -> empresaService.cadastrarEmpresa(request))
                    .isInstanceOf(EmailExistenteException.class)
                    .hasMessageContaining("Email já cadastrado");
        }

        verify(empresaRepository, never()).save(any());
    }

    @Test
    void deveBuscarEmpresasComFiltrosEPaginacao() {
        var page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);
        when(empresaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(mapper.toResponseDto(entity)).thenReturn(response);

        try (MockedStatic<CnpjValidator> cnpj = cnpjValido()) {
            PageResponse<EmpresaResponseDTO> result = empresaService.getAllEmpresaSorted(
                    request.getDocumentNumber(), "Pet Shop", request.getEmail(), StatusEmpresa.AGUARDANDO_CONTRATO,
                    0, 10, SortField.RAZAO_SOCIAL, DirectionField.ASC
            );

            assertThat(result.getContent()).containsExactly(response);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getTotalPages()).isEqualTo(1);
        }
    }

    @Test
    void buscaDeveRejeitarCnpjInvalido() {
        try (MockedStatic<CnpjValidator> cnpj = Mockito.mockStatic(CnpjValidator.class)) {
            cnpj.when(() -> CnpjValidator.isCnpjValid("00000000000000")).thenReturn(false);

            assertThatThrownBy(() -> empresaService.getAllEmpresaSorted(
                    "00000000000000", null, null, null, 0, 10,
                    SortField.RAZAO_SOCIAL, DirectionField.ASC
            )).isInstanceOf(CnpjInvalidException.class).hasMessage("CNPJ informado é inválido.");
        }
        verify(empresaRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void buscaDeveValidarPaginaETamanho() {
        assertThatThrownBy(() -> empresaService.getAllEmpresaSorted(
                null, null, null, null, -1, 10, SortField.RAZAO_SOCIAL, DirectionField.ASC
        )).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> empresaService.getAllEmpresaSorted(
                null, null, null, null, 0, 101, SortField.RAZAO_SOCIAL, DirectionField.ASC
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void buscaSemResultadosDeveRetornarPaginaVazia() {
        when(empresaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        PageResponse<EmpresaResponseDTO> result = empresaService.getAllEmpresaSorted(
                null, null, null, null, 0, 10, SortField.RAZAO_SOCIAL, DirectionField.ASC
        );

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void deveBuscarEmpresaPorId() {
        when(empresaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(mapper.toResponseDto(entity)).thenReturn(response);
        assertThat(empresaService.getEmpresaById(entity.getId())).isEqualTo(response);
    }

    @Test
    void buscaPorIdDeveFalharQuandoEmpresaNaoExiste() {
        UUID id = UUID.randomUUID();
        when(empresaRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> empresaService.getEmpresaById(id))
                .isInstanceOf(EmpresaNaoEncontradaException.class).hasMessageContaining(id.toString());
    }

    @Test
    void deveExcluirEmpresaExistente() {
        when(empresaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        empresaService.deleteEmpresaById(entity.getId());
        verify(empresaRepository).delete(entity);
    }

    @Test
    void exclusaoDeveFalharQuandoEmpresaNaoExiste() {
        UUID id = UUID.randomUUID();
        when(empresaRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> empresaService.deleteEmpresaById(id)).isInstanceOf(EmpresaNaoEncontradaException.class);
        verify(empresaRepository, never()).delete((Empresa) any());
    }

    @Test
    void devePermitirManterOProprioEmailNaEdicao() {
        UpdateEmpresaRequestDto update = new UpdateEmpresaRequestDto();
        update.setEmail(entity.getEmail());
        update.setNomeFantasia("Pet Atualizado");

        when(empresaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(empresaRepository.existsByEmailAndIdNot(entity.getEmail(), entity.getId())).thenReturn(false);
        when(empresaRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponseDto(entity)).thenReturn(response);

        assertThat(empresaService.editEmpresaById(entity.getId(), update)).isEqualTo(response);
        verify(empresaRepository).save(entity);
    }

    @Test
    void deveRejeitarEmailDeOutraEmpresaNaEdicao() {
        UpdateEmpresaRequestDto update = new UpdateEmpresaRequestDto();
        update.setEmail("outra@empresa.com");

        when(empresaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(empresaRepository.existsByEmailAndIdNot(update.getEmail(), entity.getId())).thenReturn(true);

        assertThatThrownBy(() -> empresaService.editEmpresaById(entity.getId(), update))
                .isInstanceOf(EmailExistenteException.class);
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void edicaoSemEmailNaoDeveConsultarDuplicidade() {
        UpdateEmpresaRequestDto update = new UpdateEmpresaRequestDto();
        update.setNomeFantasia("Novo Nome");

        when(empresaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(empresaRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponseDto(entity)).thenReturn(response);

        empresaService.editEmpresaById(entity.getId(), update);
        verify(empresaRepository, never()).existsByEmailAndIdNot(any(), any());
    }

    @Test
    void edicaoDeveFalharQuandoEmpresaNaoExiste() {
        UUID id = UUID.randomUUID();
        when(empresaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empresaService.editEmpresaById(id, new UpdateEmpresaRequestDto()))
                .isInstanceOf(EmpresaNaoEncontradaException.class);
        verify(empresaRepository, never()).save(any());
    }

    private void prepararNovoCadastro() {
        when(empresaRepository.existsByDocumentNumber(request.getDocumentNumber())).thenReturn(false);
        when(empresaRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(empresaRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponseDto(entity)).thenReturn(response);
    }

    private MockedStatic<CnpjValidator> cnpjValido() {
        MockedStatic<CnpjValidator> cnpj = Mockito.mockStatic(CnpjValidator.class);
        cnpj.when(() -> CnpjValidator.isCnpjValid(request.getDocumentNumber())).thenReturn(true);
        return cnpj;
    }
}

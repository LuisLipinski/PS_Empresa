package com.mypetadmin.ps_empresa.service.impl;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.exception.EmpresaExistenteException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmpresaServiceImplTest {

    @InjectMocks
    private EmpresaServiceImpl empresaService;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EmpresaMapper mapper;

    private EmpresaRequestDTO requestDTO;
    private Empresa empresa;
    private EmpresaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new EmpresaRequestDTO(
                "20192496000150",
                "Teste Razao Social",
                "Teste Nome Fantasia",
                "41999999999",
                "email@teste.com",
                "Nome Teste",
                "Rua exemplo",
                "123",
                null,
                "Bairro Teste",
                "Cidade Teste",
                "PR",
                "01234567"
        );

        empresa = Empresa.builder()
                .documentNumber(requestDTO.getDocumentNumber())
                .razaoSocial(requestDTO.getRazaoSocial())
                .nomeFantasia(requestDTO.getNomeFantasia())
                .telefone(requestDTO.getTelefone())
                .email(requestDTO.getEmail())
                .cep(requestDTO.getCep())
                .endereco("Rua exemplo, 123, Bairro Teste")
                .cidade(requestDTO.getCidade())
                .estado(requestDTO.getEstado())
                .status("PENDENTE ATVACAO")
                .build();
        responseDTO = EmpresaResponseDTO.builder()
                .documentNumber(requestDTO.getDocumentNumber())
                .razaoSocial(requestDTO.getRazaoSocial())
                .nomeFantasia(requestDTO.getNomeFantasia())
                .telefone(requestDTO.getTelefone())
                .email(requestDTO.getEmail())
                .cep(requestDTO.getCep())
                .cidade(requestDTO.getCidade())
                .estado(requestDTO.getEstado())
                .status("PENDENTE ATIVACAO")
                .build();
    }

    @Test
    void deveCadastrarEmpresaComSucesso() {
        when(empresaRepository.existsByDocumentNumber(requestDTO.getDocumentNumber())).thenReturn(false);

        try (MockedStatic<CnpjValidator> mockedCnpj = Mockito.mockStatic(CnpjValidator.class)) {
            mockedCnpj.when(() -> CnpjValidator.isCnpjValid(requestDTO.getDocumentNumber())).thenReturn(true);

            when(mapper.toEntity(requestDTO)).thenReturn(empresa);
            when(empresaRepository.save(empresa)).thenReturn(empresa);
            when(mapper.toResponseDto(empresa)).thenReturn(responseDTO);

            EmpresaResponseDTO result = empresaService.cadastrarEmpresa(requestDTO);

            assertNotNull(result);
            assertEquals(responseDTO.getDocumentNumber(), result.getDocumentNumber());
            verify(empresaRepository).save(empresa);
        }
    }

    @Test
    void devoLancarExcecaoQuandoCnpjExistente() {
        when(empresaRepository.existsByDocumentNumber(requestDTO.getDocumentNumber())).thenReturn(true);

        EmpresaExistenteException exception = assertThrows(EmpresaExistenteException.class,
                () -> empresaService.cadastrarEmpresa(requestDTO));

        assertEquals("CNPJ já cadastrado no sistema.", exception.getMessage());
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoCnpjInvalido() {
        when(empresaRepository.existsByDocumentNumber(requestDTO.getDocumentNumber())).thenReturn(false);

        try (MockedStatic<CnpjValidator> mockedCnpj = Mockito.mockStatic(CnpjValidator.class)) {
            mockedCnpj.when(() -> CnpjValidator.isCnpjValid(requestDTO.getDocumentNumber())).thenReturn(false);

            IllegalArgumentException exception =assertThrows(IllegalArgumentException.class,
                    () -> empresaService.cadastrarEmpresa(requestDTO));

            assertEquals("CNPJ inválido.", exception.getMessage());
            verify(empresaRepository, never()).save(any());
        }



    }
}

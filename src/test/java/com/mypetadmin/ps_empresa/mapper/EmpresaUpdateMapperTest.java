package com.mypetadmin.ps_empresa.mapper;

import com.mypetadmin.ps_empresa.dto.UpdateEmpresaRequestDto;
import com.mypetadmin.ps_empresa.model.Empresa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmpresaUpdateMapperTest {

    private Empresa empresa;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setNomeFantasia("Pet Love");
        empresa.setTelefone("11999999999");
        empresa.setEmail("old@email.com");
        empresa.setNomeTitular("Luis");
        empresa.setCep("12345678");
        empresa.setCidade("São Paulo");
        empresa.setEstado("SP");
        empresa.setEndereco("Rua das Flores, 100 - Apto 12, Centro");
    }

    @Test
    void testUpdateAllFields() {
        UpdateEmpresaRequestDto dto = new UpdateEmpresaRequestDto();
        dto.setNomeFantasia("Pet Atualizado");
        dto.setTelefone("11888888888");
        dto.setEmail("novo@email.com");
        dto.setNomeTitular("Henrique");
        dto.setCep("87654321");
        dto.setCidade("Rio de Janeiro");
        dto.setEstado("RJ");
        dto.setRua("Av Brasil");
        dto.setNumero("200");
        dto.setComplemento("Bloco B");
        dto.setBairro("Copacabana");

        EmpresaUpdateMapper.updateEntityFromDto(empresa, dto);

        assertEquals("Pet Atualizado", empresa.getNomeFantasia());
        assertEquals("11888888888", empresa.getTelefone());
        assertEquals("novo@email.com", empresa.getEmail());
        assertEquals("Henrique", empresa.getNomeTitular());
        assertEquals("87654321", empresa.getCep());
        assertEquals("Rio de Janeiro", empresa.getCidade());
        assertEquals("RJ", empresa.getEstado());
        assertEquals("Av Brasil, 200 - Bloco B, Copacabana", empresa.getEndereco());
    }

    @Test
    void testPartialUpdateMaintainsOldValues() {
        UpdateEmpresaRequestDto dto = new UpdateEmpresaRequestDto();
        dto.setTelefone("11777777777");

        EmpresaUpdateMapper.updateEntityFromDto(empresa, dto);

        assertEquals("Pet Love", empresa.getNomeFantasia()); // não alterado
        assertEquals("11777777777", empresa.getTelefone()); // alterado
        assertEquals("old@email.com", empresa.getEmail()); // não alterado
        assertEquals("Rua das Flores, 100 - Apto 12, Centro", empresa.getEndereco()); // mantido
    }

    @Test
    void testUpdateOnlyBairro() {
        UpdateEmpresaRequestDto dto = new UpdateEmpresaRequestDto();
        dto.setBairro("Novo Bairro");

        EmpresaUpdateMapper.updateEntityFromDto(empresa, dto);

        assertEquals("Rua das Flores, 100 - Apto 12, Novo Bairro", empresa.getEndereco());
    }

    @Test
    void testNoUpdateKeepsAllFields() {
        UpdateEmpresaRequestDto dto = new UpdateEmpresaRequestDto();

        EmpresaUpdateMapper.updateEntityFromDto(empresa, dto);

        assertEquals("Pet Love", empresa.getNomeFantasia());
        assertEquals("11999999999", empresa.getTelefone());
        assertEquals("old@email.com", empresa.getEmail());
        assertEquals("Luis", empresa.getNomeTitular());
        assertEquals("12345678", empresa.getCep());
        assertEquals("São Paulo", empresa.getCidade());
        assertEquals("SP", empresa.getEstado());
        assertEquals("Rua das Flores, 100 - Apto 12, Centro", empresa.getEndereco());
    }

}

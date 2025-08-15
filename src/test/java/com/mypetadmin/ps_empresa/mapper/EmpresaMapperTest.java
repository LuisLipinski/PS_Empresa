package com.mypetadmin.ps_empresa.mapper;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.model.Empresa;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

public class EmpresaMapperTest {

    EmpresaMapper mapper = new EmpresaMapper();

    @Test
    void deveMapearParaEntityComComplemento() {
        EmpresaRequestDTO dto = new EmpresaRequestDTO();
        dto.setDocumentNumber("51455244000108");
        dto.setRazaoSocial("My Pet Ltda");
        dto.setNomeFantasia("My Pet");
        dto.setTelefone("41999999999");
        dto.setEmail("contato@mypet.com");
        dto.setRua("Rua dos Pets");
        dto.setNumero("123");
        dto.setComplemento("Sala 1");
        dto.setBairro("Centro");
        dto.setCidade("São Paulo");
        dto.setEstado("SP");
        dto.setCep("8090600");

        Empresa entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getDocumentNumber()).isEqualTo(dto.getDocumentNumber());
        assertThat(entity.getRazaoSocial()).isEqualTo(dto.getRazaoSocial());
        assertThat(entity.getNomeFantasia()).isEqualTo(dto.getNomeFantasia());
        assertThat(entity.getTelefone()).isEqualTo(dto.getTelefone());
        assertThat(entity.getEmail()).isEqualTo(dto.getEmail());
        assertThat(entity.getCep()).isEqualTo(dto.getCep());
        assertThat(entity.getCidade()).isEqualTo(dto.getCidade());
        assertThat(entity.getEstado()).isEqualTo(dto.getEstado());
        assertThat(entity.getEndereco()).isEqualTo("Rua dos Pets, 123 - Sala 1, Centro");
        assertThat(entity.getStatus()).isEqualTo("PENDENTE ATIVACAO");
        assertThat(entity.getDataCriacao()).isNotNull();
    }

    @Test
    void deveMapearParaEntitySemComplemento() {
        EmpresaRequestDTO dto = new EmpresaRequestDTO();
        dto.setDocumentNumber("51455244000108");
        dto.setRazaoSocial("My Pet Ltda");
        dto.setNomeFantasia("My Pet");
        dto.setTelefone("41999999999");
        dto.setEmail("contato@mypet.com");
        dto.setRua("Rua dos Pets");
        dto.setNumero("123");
        dto.setBairro("Centro");
        dto.setCidade("São Paulo");
        dto.setEstado("SP");
        dto.setCep("8090600");

        Empresa entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getDocumentNumber()).isEqualTo(dto.getDocumentNumber());
        assertThat(entity.getRazaoSocial()).isEqualTo(dto.getRazaoSocial());
        assertThat(entity.getNomeFantasia()).isEqualTo(dto.getNomeFantasia());
        assertThat(entity.getTelefone()).isEqualTo(dto.getTelefone());
        assertThat(entity.getEmail()).isEqualTo(dto.getEmail());
        assertThat(entity.getCep()).isEqualTo(dto.getCep());
        assertThat(entity.getCidade()).isEqualTo(dto.getCidade());
        assertThat(entity.getEstado()).isEqualTo(dto.getEstado());
        assertThat(entity.getEndereco()).isEqualTo("Rua dos Pets, 123, Centro");
        assertThat(entity.getStatus()).isEqualTo("PENDENTE ATIVACAO");
        assertThat(entity.getDataCriacao()).isNotNull();
    }


    @Test
    void deveMapearParaResponseDTO() {

        EmpresaMapper empresaMapper = new EmpresaMapper();
        UUID id = UUID.randomUUID();
        Empresa entity = Empresa.builder()
                .id(id)
                .documentNumber("51455244000108")
                .razaoSocial("My Pet Ltda")
                .nomeFantasia("My Pet")
                .telefone("41999999999")
                .email("contato@mypet.com")
                .cep("01001000")
                .cidade("São Paulo")
                .estado("SP")
                .endereco("Rua dos Pets, 123 - Sala 1, Centro")
                .status(StatusEmpresa.AGUARDANDO_PAGAMENTO)
                .build();

        EmpresaResponseDTO responseDTO = empresaMapper.toResponseDto(entity);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(id);
        assertThat(responseDTO.getDocumentNumber()).isEqualTo("51455244000108");
        assertThat(responseDTO.getRazaoSocial()).isEqualTo("My Pet Ltda");
        assertThat(responseDTO.getNomeFantasia()).isEqualTo("My Pet");
        assertThat(responseDTO.getTelefone()).isEqualTo("41999999999");
        assertThat(responseDTO.getEmail()).isEqualTo("contato@mypet.com");
        assertThat(responseDTO.getCep()).isEqualTo("01001000");
        assertThat(responseDTO.getCidade()).isEqualTo("São Paulo");
        assertThat(responseDTO.getEstado()).isEqualTo("SP");
        assertThat(responseDTO.getEndereco()).isEqualTo("Rua dos Pets, 123 - Sala 1, Centro");
        assertThat(responseDTO.getStatus()).isEqualTo("PENDENTE ATIVACAO");
    }
}

package com.mypetadmin.ps_empresa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@Schema(name = "Empresa Cadastrada", description = "Representação de uma empresa cadastrada no sistema")
public class EmpresaResponseDTO {

    private UUID id;
    private String documentNumber;
    private String razaoSocial;
    private String nomeFantasia;
    private String telefone;
    private String email;
    private String cep;
    private String cidade;
    private String estado;
    private String endereco;
    private String status;
}

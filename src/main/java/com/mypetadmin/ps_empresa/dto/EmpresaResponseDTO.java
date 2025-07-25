package com.mypetadmin.ps_empresa.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
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

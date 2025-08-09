package com.mypetadmin.ps_empresa.mapper;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.model.Empresa;
import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper {

    public Empresa toEntity(EmpresaRequestDTO dto) {
        return Empresa.builder()
                .documentNumber(dto.getDocumentNumber())
                .razaoSocial(dto.getRazaoSocial())
                .nomeFantasia(dto.getNomeFantasia())
                .telefone(dto.getTelefone())
                .email(dto.getEmail())
                .nomeTitular((dto.getNomeTitular()))
                .cep(dto.getCep())
                .endereco(String.format("%s, %s%s%s",
                        dto.getRua(),
                        dto.getNumero(),
                        dto.getComplemento() != null && !dto.getComplemento().isEmpty()
                                ? " - " + dto.getComplemento() + ", "
                                : ", ",
                        dto.getBairro()
                        )
                )
                .cidade(dto.getCidade())
                .estado(dto.getEstado())
                .build();

    }

    public EmpresaResponseDTO toResponseDto(Empresa empresa) {
        return EmpresaResponseDTO.builder()
                .id(empresa.getId())
                .documentNumber(empresa.getDocumentNumber())
                .razaoSocial(empresa.getRazaoSocial())
                .nomeFantasia(empresa.getNomeFantasia())
                .telefone(empresa.getTelefone())
                .email(empresa.getEmail())
                .nomeTitular(empresa.getNomeTitular())
                .cep(empresa.getCep())
                .endereco(empresa.getEndereco())
                .cidade(empresa.getCidade())
                .estado(empresa.getEstado())
                .status(empresa.getStatus())
                .build();
    }
}

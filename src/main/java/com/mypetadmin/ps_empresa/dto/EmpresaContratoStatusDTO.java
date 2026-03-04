package com.mypetadmin.ps_empresa.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EmpresaContratoStatusDTO {
    private UUID empresaId;
    private String statusContrato;
}

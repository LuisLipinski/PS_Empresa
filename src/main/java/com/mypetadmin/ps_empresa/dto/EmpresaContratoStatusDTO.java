package com.mypetadmin.ps_empresa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EmpresaContratoStatusDTO {

    @NotNull(message = "empresaId é obrigatório")
    private UUID empresaId;

    @NotBlank(message = "statusContrato é obrigatório")
    private String statusContrato;
}

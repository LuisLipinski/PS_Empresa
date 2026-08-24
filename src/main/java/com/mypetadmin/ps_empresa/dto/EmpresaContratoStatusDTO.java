package com.mypetadmin.ps_empresa.dto;

import com.mypetadmin.ps_empresa.enums.StatusContrato;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EmpresaContratoStatusDTO {

    @NotNull
    private UUID empresaId;

    @NotNull
    private StatusContrato statusContrato;
}

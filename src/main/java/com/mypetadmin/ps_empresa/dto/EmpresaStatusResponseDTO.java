package com.mypetadmin.ps_empresa.dto;

import com.mypetadmin.ps_empresa.enums.StatusEmpresa;

import java.util.UUID;

public record EmpresaStatusResponseDTO(UUID empresaId, StatusEmpresa status) {
}

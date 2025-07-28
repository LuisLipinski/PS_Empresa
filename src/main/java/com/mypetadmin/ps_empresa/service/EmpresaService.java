package com.mypetadmin.ps_empresa.service;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface EmpresaService {
    EmpresaResponseDTO cadastrarEmpresa(EmpresaRequestDTO dto);

   void atualizarStatus(@NotNull UUID empresaId, @NotNull String novoStatus);
}

package com.mypetadmin.ps_empresa.service;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.dto.UpdateEmpresaRequestDto;
import com.mypetadmin.ps_empresa.enums.DirectionField;
import com.mypetadmin.ps_empresa.enums.SortField;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface EmpresaService {
    EmpresaResponseDTO cadastrarEmpresa(EmpresaRequestDTO dto);

    void atualizarStatus(@NotNull UUID empresaId, @NotNull StatusEmpresa novoStatus);

    List<EmpresaResponseDTO> getAllEmpresaSorted(String documentNumber, String razaoSocial, String email, StatusEmpresa status, SortField sortField, DirectionField directionField);

    EmpresaResponseDTO getEmpresaById(UUID id);

    void deleteEmpresaById(UUID id);

    EmpresaResponseDTO editEmpresaById(UUID empresaId, UpdateEmpresaRequestDto updateEmpresa);

    void ativarEmpresaPorContrato(UUID empresaId);
}

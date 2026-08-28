package com.mypetadmin.ps_empresa.service;

import com.mypetadmin.ps_empresa.dto.EmpresaContratoStatusDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.dto.PageResponse;
import com.mypetadmin.ps_empresa.dto.UpdateEmpresaRequestDto;
import com.mypetadmin.ps_empresa.enums.DirectionField;
import com.mypetadmin.ps_empresa.enums.SortField;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;

import java.util.UUID;

public interface EmpresaService {
    EmpresaResponseDTO cadastrarEmpresa(EmpresaRequestDTO dto);

    EmpresaResponseDTO cadastrarEmpresaOnboarding(EmpresaRequestDTO dto, UUID onboardingId);

    void sincronizarStatusComContrato(EmpresaContratoStatusDTO dto);

    PageResponse<EmpresaResponseDTO> getAllEmpresaSorted(String documentNumber, String razaoSocial, String email, StatusEmpresa status, int page, int size, SortField sortField, DirectionField directionField);

    EmpresaResponseDTO getEmpresaById(UUID id);

    void deleteEmpresaById(UUID id);

    EmpresaResponseDTO editEmpresaById(UUID empresaId, UpdateEmpresaRequestDto updateEmpresa);
}

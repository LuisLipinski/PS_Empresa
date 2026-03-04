package com.mypetadmin.ps_empresa.service;

import com.mypetadmin.ps_empresa.dto.*;
import com.mypetadmin.ps_empresa.enums.DirectionField;
import com.mypetadmin.ps_empresa.enums.SortField;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;


import java.util.List;
import java.util.UUID;

public interface EmpresaService {
    EmpresaResponseDTO cadastrarEmpresa(EmpresaRequestDTO dto);

    void sincronizarStatusComContrato(EmpresaContratoStatusDTO dto);

    PageResponse<EmpresaResponseDTO> getAllEmpresaSorted(String documentNumber, String razaoSocial, String email, StatusEmpresa status, int page, int size, SortField sortField, DirectionField directionField);

    EmpresaResponseDTO getEmpresaById(UUID id);

    void deleteEmpresaById(UUID id);

    EmpresaResponseDTO editEmpresaById(UUID empresaId, UpdateEmpresaRequestDto updateEmpresa);
}

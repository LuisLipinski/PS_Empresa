package com.mypetadmin.ps_empresa.service;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;

public interface EmpresaService {
    EmpresaResponseDTO cadastrarEmpresa(EmpresaRequestDTO dto);
}

package com.mypetadmin.ps_empresa.controller;

import com.mypetadmin.ps_empresa.dto.EmpresaContratoStatusDTO;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/contratos")
@RequiredArgsConstructor
public class ContratoCallbackController {

    private final EmpresaService empresaService;

    @PatchMapping("/status")
    public void atualizarEmpresaPorContrato(@Valid @RequestBody EmpresaContratoStatusDTO dto) {
        empresaService.sincronizarStatusComContrato(dto);
    }
}

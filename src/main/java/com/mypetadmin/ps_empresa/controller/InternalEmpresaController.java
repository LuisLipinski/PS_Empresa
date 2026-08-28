package com.mypetadmin.ps_empresa.controller;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaStatusResponseDTO;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/internal/empresas")
@RequiredArgsConstructor
public class InternalEmpresaController {

    private static final String ONBOARDING_ID_HEADER = "X-Onboarding-Id";

    private final EmpresaService empresaService;

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> cadastrarEmpresa(@Valid @RequestBody EmpresaRequestDTO request) {
        EmpresaResponseDTO empresa = empresaService.cadastrarEmpresa(request);
        return created(empresa);
    }

    @PostMapping("/onboarding")
    public ResponseEntity<EmpresaResponseDTO> cadastrarEmpresaOnboarding(
            @RequestHeader(ONBOARDING_ID_HEADER) UUID onboardingId,
            @Valid @RequestBody EmpresaRequestDTO request) {
        EmpresaResponseDTO empresa = empresaService.cadastrarEmpresaOnboarding(request, onboardingId);
        return created(empresa);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<EmpresaStatusResponseDTO> consultarStatus(@PathVariable UUID id) {
        EmpresaResponseDTO empresa = empresaService.getEmpresaById(id);
        return ResponseEntity.ok(new EmpresaStatusResponseDTO(empresa.getId(), empresa.getStatus()));
    }

    private ResponseEntity<EmpresaResponseDTO> created(EmpresaResponseDTO empresa) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/internal/empresas/{id}")
                .buildAndExpand(empresa.getId())
                .toUri();
        return ResponseEntity.created(location).body(empresa);
    }
}

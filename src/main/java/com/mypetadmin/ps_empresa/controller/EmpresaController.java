package com.mypetadmin.ps_empresa.controller;

import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.dto.PageResponse;
import com.mypetadmin.ps_empresa.dto.UpdateEmpresaRequestDto;
import com.mypetadmin.ps_empresa.enums.DirectionField;
import com.mypetadmin.ps_empresa.enums.SortField;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.security.TenantAccessGuard;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;
    private final TenantAccessGuard tenantAccessGuard;

    @Operation(summary = "Busca empresas com filtros, paginação e ordenação — somente uso interno")
    @GetMapping
    public ResponseEntity<PageResponse<EmpresaResponseDTO>> buscarEmpresas(
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false) String razaoSocial,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) StatusEmpresa status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DOCUMENT_NUMBER") SortField sortField,
            @RequestParam(defaultValue = "ASC") DirectionField directionField) {

        return ResponseEntity.ok(
                empresaService.getAllEmpresaSorted(
                        documentNumber,
                        razaoSocial,
                        email,
                        status,
                        page,
                        size,
                        sortField,
                        directionField
                )
        );
    }

    @Operation(summary = "Busca a empresa do tenant autenticado")
    @GetMapping("/me")
    public ResponseEntity<EmpresaResponseDTO> getMinhaEmpresa(Authentication authentication) {
        UUID empresaId = tenantAccessGuard.requireEmpresaId(authentication);
        return ResponseEntity.ok(empresaService.getEmpresaById(empresaId));
    }

    @Operation(summary = "Busca uma empresa pelo id, respeitando o tenant autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empresa encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Empresa pertence a outro tenant"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> getEmpresaById(@PathVariable UUID id,
                                                              Authentication authentication) {
        tenantAccessGuard.requireAccess(authentication, id);
        return ResponseEntity.ok(empresaService.getEmpresaById(id));
    }

    @Operation(summary = "Atualiza parcialmente a empresa do tenant autenticado")
    @PatchMapping("/me")
    public ResponseEntity<EmpresaResponseDTO> editMinhaEmpresa(
            Authentication authentication,
            @Valid @RequestBody UpdateEmpresaRequestDto updateEmpresa) {
        UUID empresaId = tenantAccessGuard.requireEmpresaId(authentication);
        return ResponseEntity.ok(empresaService.editEmpresaById(empresaId, updateEmpresa));
    }

    @Operation(summary = "Atualiza parcialmente os dados de uma empresa, respeitando o tenant autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empresa atualizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Empresa pertence a outro tenant"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflito de dados")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> editEmpresaById(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody UpdateEmpresaRequestDto updateEmpresa) {
        tenantAccessGuard.requireAccess(authentication, id);
        return ResponseEntity.ok(empresaService.editEmpresaById(id, updateEmpresa));
    }

    @Operation(summary = "Exclui uma empresa pelo id, respeitando o tenant autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Empresa excluída"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Empresa pertence a outro tenant"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpresaById(@PathVariable UUID id,
                                                   Authentication authentication) {
        tenantAccessGuard.requireAccess(authentication, id);
        empresaService.deleteEmpresaById(id);
        return ResponseEntity.noContent().build();
    }
}

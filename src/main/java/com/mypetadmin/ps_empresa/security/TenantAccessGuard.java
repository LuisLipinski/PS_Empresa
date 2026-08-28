package com.mypetadmin.ps_empresa.security;

import com.mypetadmin.ps_empresa.exception.TenantAccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TenantAccessGuard {

    public UUID requireEmpresaId(Authentication authentication) {
        if (isInternal(authentication)) {
            throw new TenantAccessDeniedException("Chamadas internas devem informar explicitamente a empresa alvo.");
        }
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof JwtTenantPrincipal principal)) {
            throw new TenantAccessDeniedException("Contexto de tenant ausente ou inválido.");
        }
        return principal.empresaId();
    }

    public void requireAccess(Authentication authentication, UUID targetEmpresaId) {
        if (isInternal(authentication)) {
            return;
        }
        UUID authenticatedEmpresaId = requireEmpresaId(authentication);
        if (!authenticatedEmpresaId.equals(targetEmpresaId)) {
            throw new TenantAccessDeniedException("Acesso a empresa de outro tenant não é permitido.");
        }
    }

    private boolean isInternal(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_INTERNAL".equals(authority.getAuthority()));
    }
}

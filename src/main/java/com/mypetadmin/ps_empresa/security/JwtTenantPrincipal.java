package com.mypetadmin.ps_empresa.security;

import java.security.Principal;
import java.util.UUID;

public record JwtTenantPrincipal(UUID userId, UUID empresaId) implements Principal {

    @Override
    public String getName() {
        return userId.toString();
    }
}

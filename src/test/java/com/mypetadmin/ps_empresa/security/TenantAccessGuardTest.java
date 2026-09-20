package com.mypetadmin.ps_empresa.security;

import com.mypetadmin.ps_empresa.exception.TenantAccessDeniedException;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TenantAccessGuardTest {

    private final TenantAccessGuard guard = new TenantAccessGuard();
    private final UUID userId = UUID.randomUUID();
    private final UUID empresaId = UUID.randomUUID();

    @Test
    void retornaEmpresaDoPrincipalJwt() {
        assertThat(guard.requireEmpresaId(jwtAuthentication(empresaId))).isEqualTo(empresaId);
    }

    @Test
    void permiteSomenteMesmaEmpresaParaJwt() {
        assertThatCode(() -> guard.requireAccess(jwtAuthentication(empresaId), empresaId)).doesNotThrowAnyException();
        assertThatThrownBy(() -> guard.requireAccess(jwtAuthentication(empresaId), UUID.randomUUID()))
                .isInstanceOf(TenantAccessDeniedException.class);
    }

    @Test
    void permiteAcessoExplicitoParaServicoInterno() {
        var internal = new UsernamePasswordAuthenticationToken(
                "internal-service", null, List.of(new SimpleGrantedAuthority("ROLE_INTERNAL")));
        assertThatCode(() -> guard.requireAccess(internal, UUID.randomUUID())).doesNotThrowAnyException();
        assertThatThrownBy(() -> guard.requireEmpresaId(internal))
                .isInstanceOf(TenantAccessDeniedException.class);
    }

    @Test
    void rejeitaContextoAusenteOuPrincipalNaoTipado() {
        assertThatThrownBy(() -> guard.requireEmpresaId(null))
                .isInstanceOf(TenantAccessDeniedException.class);
        var invalid = new UsernamePasswordAuthenticationToken("user", null, List.of());
        assertThatThrownBy(() -> guard.requireEmpresaId(invalid))
                .isInstanceOf(TenantAccessDeniedException.class);
    }

    private UsernamePasswordAuthenticationToken jwtAuthentication(UUID tenantId) {
        return new UsernamePasswordAuthenticationToken(
                new JwtTenantPrincipal(userId, tenantId),
                null,
                List.of(new SimpleGrantedAuthority("MASTER")));
    }
}

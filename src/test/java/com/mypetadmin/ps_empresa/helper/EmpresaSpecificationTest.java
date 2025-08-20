package com.mypetadmin.ps_empresa.helper;

import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.model.Empresa;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;

public class EmpresaSpecificationTest {
    @Test
    void hasDocumentNumber_naoLancaErro() {
        Specification<Empresa> spec = EmpresaSpecification.hasDocumentNumber(null);
        assertThat(spec).isNotNull();

        spec = EmpresaSpecification.hasDocumentNumber("12345678901234");
        assertThat(spec).isNotNull();
    }

    @Test
    void hasDocumentNumber_quandoValorValido_naoRetornaNull() {
        Specification<Empresa> spec = EmpresaSpecification.hasDocumentNumber("34222351000169");
        assertThat(spec).isNotNull();
    }

    @Test
    void hasRazaoSocial_naoLancaErro() {
        Specification<Empresa> spec = EmpresaSpecification.hasRazaoSocial(null);
        assertThat(spec).isNotNull();

        spec = EmpresaSpecification.hasRazaoSocial("Pet Shop");
        assertThat(spec).isNotNull();
    }

    @Test
    void hasRazaoSocial_quandoValorValido_naoRetornaNull() {
        Specification<Empresa> spec = EmpresaSpecification.hasRazaoSocial("Pet Shop");
        assertThat(spec).isNotNull();
    }

    @Test
    void hasEmail_naoLancaErro() {
        Specification<Empresa> spec = EmpresaSpecification.hasEmail(null);
        assertThat(spec).isNotNull();

        spec = EmpresaSpecification.hasEmail("teste@empresa.com");
        assertThat(spec).isNotNull();
    }

    @Test
    void hasEmail_quandoValorValido_naoRetornaNull() {
        Specification<Empresa> spec = EmpresaSpecification.hasEmail("teste@empresa.com");
        assertThat(spec).isNotNull();
    }

    @Test
    void hasStatus_naoLancaErro() {
        Specification<Empresa> spec = EmpresaSpecification.hasStatus(null);
        assertThat(spec).isNotNull();

        spec = EmpresaSpecification.hasStatus(StatusEmpresa.ATIVO);
        assertThat(spec).isNotNull();
    }
    @Test
    void hasStatus_quandoValorValido_naoRetornaNull() {
        Specification<Empresa> spec = EmpresaSpecification.hasStatus(StatusEmpresa.ATIVO);
        assertThat(spec).isNotNull();
    }
}

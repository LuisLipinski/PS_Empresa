package com.mypetadmin.ps_empresa.helper;

import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.model.Empresa;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EmpresaSpecificationTest {

    @Test
    void deveCriarSpecificationParaDocumentNumber() {
        Specification<Empresa> specNull = EmpresaSpecification.hasDocumentNumber(null);
        Specification<Empresa> specValue = EmpresaSpecification.hasDocumentNumber("34222351000169");

        assertThat(specNull).isNotNull();
        assertThat(specValue).isNotNull();
    }

    @Test
    void deveCriarSpecificationParaRazaoSocial() {
        Specification<Empresa> specNull = EmpresaSpecification.hasRazaoSocial(null);
        Specification<Empresa> specValue = EmpresaSpecification.hasRazaoSocial("Pet Shop");

        assertThat(specNull).isNotNull();
        assertThat(specValue).isNotNull();
    }

    @Test
    void deveCriarSpecificationParaNomeFantasia() {
        Specification<Empresa> specNull = EmpresaSpecification.hasNomeFantasia(null);
        Specification<Empresa> specValue = EmpresaSpecification.hasNomeFantasia("Pet Shop Dogão");

        assertThat(specNull).isNotNull();
        assertThat(specValue).isNotNull();
    }

    @Test
    void deveCriarSpecificationParaCep() {
        Specification<Empresa> specNull = EmpresaSpecification.hasCep(null);
        Specification<Empresa> specValue = EmpresaSpecification.hasCep("99999999");

        assertThat(specNull).isNotNull();
        assertThat(specValue).isNotNull();
    }

    @Test
    void deveCriarSpecificationParaEndereco() {
        Specification<Empresa> specNull = EmpresaSpecification.hasEndereco(null);
        Specification<Empresa> specValue = EmpresaSpecification.hasEndereco("Rua Exemplo");

        assertThat(specNull).isNotNull();
        assertThat(specValue).isNotNull();
    }

    @Test
    void deveCriarSpecificationParaCidade() {
        Specification<Empresa> specNull = EmpresaSpecification.hasCidade(null);
        Specification<Empresa> specValue = EmpresaSpecification.hasCidade("Curitiba");

        assertThat(specNull).isNotNull();
        assertThat(specValue).isNotNull();
    }

    @Test
    void deveCriarSpecificationParaEstado() {
        Specification<Empresa> specNull = EmpresaSpecification.hasEstado(null);
        Specification<Empresa> specValue = EmpresaSpecification.hasEstado("PR");

        assertThat(specNull).isNotNull();
        assertThat(specValue).isNotNull();
    }

    @Test
    void deveCriarSpecificationParaTelefone() {
        Specification<Empresa> specNull = EmpresaSpecification.hasTelefone(null);
        Specification<Empresa> specValue = EmpresaSpecification.hasTelefone("41999999999");

        assertThat(specNull).isNotNull();
        assertThat(specValue).isNotNull();
    }

    @Test
    void deveCriarSpecificationParaDataAtualizacaoStatusAfter() {
        Specification<Empresa> specNull = EmpresaSpecification.hasDataAtualizacaoStatusAfter(null);
        Specification<Empresa> specValue = EmpresaSpecification.hasDataAtualizacaoStatusAfter(LocalDateTime.now());

        assertThat(specNull).isNotNull();
        assertThat(specValue).isNotNull();
    }

    @Test
    void deveCriarSpecificationParaDataCriacaoAfter() {
        Specification<Empresa> specNull = EmpresaSpecification.hasDataCriacaoAfter(null);
        Specification<Empresa> specValue = EmpresaSpecification.hasDataCriacaoAfter(LocalDateTime.now());

        assertThat(specNull).isNotNull();
        assertThat(specValue).isNotNull();
    }

    @Test
    void deveCriarSpecificationParaStatus() {
        Specification<Empresa> specNull = EmpresaSpecification.hasStatus(null);
        Specification<Empresa> specValue = EmpresaSpecification.hasStatus(StatusEmpresa.ATIVO);

        assertThat(specNull).isNotNull();
        assertThat(specValue).isNotNull();
    }
}
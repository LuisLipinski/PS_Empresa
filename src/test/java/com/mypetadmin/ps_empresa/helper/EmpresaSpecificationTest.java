package com.mypetadmin.ps_empresa.helper;

import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.model.Empresa;
import com.mypetadmin.ps_empresa.repository.EmpresaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.jpa.domain.Specification.where;

@DataJpaTest
@ActiveProfiles("test")
class EmpresaSpecificationTest {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Test
    @DisplayName("Deve filtrar por documentNumber")
    void deveFiltrarPorDocumentNumber() {
        Empresa empresa1 = criarEmpresa(
                "17395568000151",
                "Pet Shop Alpha LTDA",
                "Pet Alpha",
                "41999999999",
                "80000000",
                "Rua A, 100 - Sala 1, Centro",
                "Curitiba",
                "PR",
                StatusEmpresa.ATIVO
        );

        Empresa empresa2 = criarEmpresa(
                "46017245000122",
                "Pet Shop Beta LTDA",
                "Pet Beta",
                "41888888888",
                "81000000",
                "Rua B, 200, Batel",
                "Curitiba",
                "PR",
                StatusEmpresa.AGUARDANDO_CONTRATO
        );

        empresaRepository.saveAll(List.of(empresa1, empresa2));

        Specification<Empresa> spec = where(EmpresaSpecification.hasDocumentNumber("17395568000151"));

        List<Empresa> resultado = empresaRepository.findAll(spec);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDocumentNumber()).isEqualTo("17395568000151");
        assertThat(resultado.get(0).getRazaoSocial()).isEqualTo("Pet Shop Alpha LTDA");
    }

    @Test
    @DisplayName("Deve filtrar por razão social ignorando case")
    void deveFiltrarPorRazaoSocialIgnorandoCase() {
        Empresa empresa1 = criarEmpresa(
                "17395568000151",
                "Pet Shop Alpha LTDA",
                "Pet Alpha",
                "41999999999",
                "80000000",
                "Rua A, 100 - Sala 1, Centro",
                "Curitiba",
                "PR",
                StatusEmpresa.ATIVO
        );

        Empresa empresa2 = criarEmpresa(
                "46017245000122",
                "Clínica Veterinária Beta",
                "Vet Beta",
                "41888888888",
                "81000000",
                "Rua B, 200, Batel",
                "Curitiba",
                "PR",
                StatusEmpresa.AGUARDANDO_CONTRATO
        );

        empresaRepository.saveAll(List.of(empresa1, empresa2));

        Specification<Empresa> spec = where(EmpresaSpecification.hasRazaoSocial("pet shop"));

        List<Empresa> resultado = empresaRepository.findAll(spec);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRazaoSocial()).isEqualTo("Pet Shop Alpha LTDA");
    }

    @Test
    @DisplayName("Deve filtrar por nome fantasia ignorando case")
    void deveFiltrarPorNomeFantasiaIgnorandoCase() {
        Empresa empresa1 = criarEmpresa(
                "17395568000151",
                "Pet Shop Alpha LTDA",
                "Pet Alpha",
                "41999999999",
                "80000000",
                "Rua A, 100 - Sala 1, Centro",
                "Curitiba",
                "PR",
                StatusEmpresa.ATIVO
        );

        Empresa empresa2 = criarEmpresa(
                "46017245000122",
                "Pet Shop Beta LTDA",
                "Banho e Tosa Beta",
                "41888888888",
                "81000000",
                "Rua B, 200, Batel",
                "Curitiba",
                "PR",
                StatusEmpresa.AGUARDANDO_CONTRATO
        );

        empresaRepository.saveAll(List.of(empresa1, empresa2));

        Specification<Empresa> spec = where(EmpresaSpecification.hasNomeFantasia("pet alpha"));

        List<Empresa> resultado = empresaRepository.findAll(spec);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNomeFantasia()).isEqualTo("Pet Alpha");
    }

    @Test
    @DisplayName("Deve filtrar por cidade")
    void deveFiltrarPorCidade() {
        Empresa empresa1 = criarEmpresa(
                "17395568000151",
                "Pet Shop Alpha LTDA",
                "Pet Alpha",
                "41999999999",
                "80000000",
                "Rua A, 100 - Sala 1, Centro",
                "Curitiba",
                "PR",
                StatusEmpresa.ATIVO
        );

        Empresa empresa2 = criarEmpresa(
                "46017245000122",
                "Pet Shop Beta LTDA",
                "Pet Beta",
                "41888888888",
                "81000000",
                "Rua B, 200, Centro",
                "São Paulo",
                "SP",
                StatusEmpresa.AGUARDANDO_CONTRATO
        );

        empresaRepository.saveAll(List.of(empresa1, empresa2));

        Specification<Empresa> spec = where(EmpresaSpecification.hasCidade("Curitiba"));

        List<Empresa> resultado = empresaRepository.findAll(spec);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCidade()).isEqualTo("Curitiba");
    }

    @Test
    @DisplayName("Deve filtrar por estado")
    void deveFiltrarPorEstado() {
        Empresa empresa1 = criarEmpresa(
                "17395568000151",
                "Pet Shop Alpha LTDA",
                "Pet Alpha",
                "41999999999",
                "80000000",
                "Rua A, 100 - Sala 1, Centro",
                "Curitiba",
                "PR",
                StatusEmpresa.ATIVO
        );

        Empresa empresa2 = criarEmpresa(
                "46017245000122",
                "Pet Shop Beta LTDA",
                "Pet Beta",
                "41888888888",
                "81000000",
                "Rua B, 200, Centro",
                "São Paulo",
                "SP",
                StatusEmpresa.AGUARDANDO_CONTRATO
        );

        empresaRepository.saveAll(List.of(empresa1, empresa2));

        Specification<Empresa> spec = where(EmpresaSpecification.hasEstado("SP"));

        List<Empresa> resultado = empresaRepository.findAll(spec);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo("SP");
    }

    @Test
    @DisplayName("Deve filtrar por status")
    void deveFiltrarPorStatus() {
        Empresa empresa1 = criarEmpresa(
                "17395568000151",
                "Pet Shop Alpha LTDA",
                "Pet Alpha",
                "41999999999",
                "80000000",
                "Rua A, 100 - Sala 1, Centro",
                "Curitiba",
                "PR",
                StatusEmpresa.ATIVO
        );

        Empresa empresa2 = criarEmpresa(
                "46017245000122",
                "Pet Shop Beta LTDA",
                "Pet Beta",
                "41888888888",
                "81000000",
                "Rua B, 200, Batel",
                "Curitiba",
                "PR",
                StatusEmpresa.AGUARDANDO_CONTRATO
        );

        empresaRepository.saveAll(List.of(empresa1, empresa2));

        Specification<Empresa> spec = where(EmpresaSpecification.hasStatus(StatusEmpresa.AGUARDANDO_CONTRATO));

        List<Empresa> resultado = empresaRepository.findAll(spec);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getStatus()).isEqualTo(StatusEmpresa.AGUARDANDO_CONTRATO);
    }

    @Test
    @DisplayName("Deve filtrar por telefone")
    void deveFiltrarPorTelefone() {
        Empresa empresa1 = criarEmpresa(
                "17395568000151",
                "Pet Shop Alpha LTDA",
                "Pet Alpha",
                "41999999999",
                "80000000",
                "Rua A, 100 - Sala 1, Centro",
                "Curitiba",
                "PR",
                StatusEmpresa.ATIVO
        );

        Empresa empresa2 = criarEmpresa(
                "46017245000122",
                "Pet Shop Beta LTDA",
                "Pet Beta",
                "41888888888",
                "81000000",
                "Rua B, 200, Batel",
                "Curitiba",
                "PR",
                StatusEmpresa.AGUARDANDO_CONTRATO
        );

        empresaRepository.saveAll(List.of(empresa1, empresa2));

        Specification<Empresa> spec = where(EmpresaSpecification.hasTelefone("41999999999"));

        List<Empresa> resultado = empresaRepository.findAll(spec);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTelefone()).isEqualTo("41999999999");
    }

    @Test
    @DisplayName("Deve combinar filtros de razão social e status")
    void deveCombinarFiltrosDeRazaoSocialEStatus() {
        Empresa empresa1 = criarEmpresa(
                "17395568000151",
                "Pet Shop Alpha LTDA",
                "Pet Alpha",
                "41999999999",
                "80000000",
                "Rua A, 100 - Sala 1, Centro",
                "Curitiba",
                "PR",
                StatusEmpresa.ATIVO
        );

        Empresa empresa2 = criarEmpresa(
                "46017245000122",
                "Pet Shop Alpha Filial",
                "Pet Alpha Filial",
                "41888888888",
                "81000000",
                "Rua B, 200, Batel",
                "Curitiba",
                "PR",
                StatusEmpresa.AGUARDANDO_CONTRATO
        );

        Empresa empresa3 = criarEmpresa(
                "51455244000108",
                "Clínica Vet Gama",
                "Vet Gama",
                "41777777777",
                "82000000",
                "Rua C, 300, Água Verde",
                "Curitiba",
                "PR",
                StatusEmpresa.ATIVO
        );

        empresaRepository.saveAll(List.of(empresa1, empresa2, empresa3));

        Specification<Empresa> spec = where(EmpresaSpecification.hasRazaoSocial("Pet Shop Alpha"))
                .and(EmpresaSpecification.hasStatus(StatusEmpresa.ATIVO));

        List<Empresa> resultado = empresaRepository.findAll(spec);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDocumentNumber()).isEqualTo("17395568000151");
        assertThat(resultado.get(0).getStatus()).isEqualTo(StatusEmpresa.ATIVO);
    }

    @Test
    @DisplayName("Não deve filtrar quando parâmetro for nulo")
    void naoDeveFiltrarQuandoParametroForNulo() {
        Empresa empresa1 = criarEmpresa(
                "17395568000151",
                "Pet Shop Alpha LTDA",
                "Pet Alpha",
                "41999999999",
                "80000000",
                "Rua A, 100 - Sala 1, Centro",
                "Curitiba",
                "PR",
                StatusEmpresa.ATIVO
        );

        Empresa empresa2 = criarEmpresa(
                "46017245000122",
                "Pet Shop Beta LTDA",
                "Pet Beta",
                "41888888888",
                "81000000",
                "Rua B, 200, Batel",
                "Curitiba",
                "PR",
                StatusEmpresa.AGUARDANDO_CONTRATO
        );

        empresaRepository.saveAll(List.of(empresa1, empresa2));

        Specification<Empresa> spec = where(EmpresaSpecification.hasRazaoSocial(null));

        List<Empresa> resultado = empresaRepository.findAll(spec);

        assertThat(resultado).hasSize(2);
    }

    @Test
    @DisplayName("Deve filtrar por data de criação after")
    void deveFiltrarPorDataCriacaoAfter() {
        LocalDateTime agora = LocalDateTime.now();

        Empresa empresaAntiga = criarEmpresa(
                "17395568000151",
                "Pet Shop Alpha LTDA",
                "Pet Alpha",
                "41999999999",
                "80000000",
                "Rua A, 100 - Sala 1, Centro",
                "Curitiba",
                "PR",
                StatusEmpresa.ATIVO
        );
        empresaAntiga.setDataCriacao(agora.minusDays(10));

        Empresa empresaNova = criarEmpresa(
                "46017245000122",
                "Pet Shop Beta LTDA",
                "Pet Beta",
                "41888888888",
                "81000000",
                "Rua B, 200, Batel",
                "Curitiba",
                "PR",
                StatusEmpresa.AGUARDANDO_CONTRATO
        );
        empresaNova.setDataCriacao(agora.minusDays(1));

        empresaRepository.saveAll(List.of(empresaAntiga, empresaNova));

        Specification<Empresa> spec = where(EmpresaSpecification.hasDataCriacaoAfter(agora.minusDays(5)));

        List<Empresa> resultado = empresaRepository.findAll(spec);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDocumentNumber()).isEqualTo("46017245000122");
    }

    @Test
    @DisplayName("Deve filtrar por data de atualização de status after")
    void deveFiltrarPorDataAtualizacaoStatusAfter() {
        LocalDateTime agora = LocalDateTime.now();

        Empresa empresaAntiga = criarEmpresa(
                "17395568000151",
                "Pet Shop Alpha LTDA",
                "Pet Alpha",
                "41999999999",
                "80000000",
                "Rua A, 100 - Sala 1, Centro",
                "Curitiba",
                "PR",
                StatusEmpresa.ATIVO
        );
        empresaAntiga.setDataAtualizacaoStatus(agora.minusDays(10));

        Empresa empresaNova = criarEmpresa(
                "46017245000122",
                "Pet Shop Beta LTDA",
                "Pet Beta",
                "41888888888",
                "81000000",
                "Rua B, 200, Batel",
                "Curitiba",
                "PR",
                StatusEmpresa.AGUARDANDO_CONTRATO
        );
        empresaNova.setDataAtualizacaoStatus(agora.minusDays(1));

        empresaRepository.saveAll(List.of(empresaAntiga, empresaNova));

        Specification<Empresa> spec = where(EmpresaSpecification.hasDataAtualizacaoStatusAfter(agora.minusDays(5)));

        List<Empresa> resultado = empresaRepository.findAll(spec);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDocumentNumber()).isEqualTo("46017245000122");
    }

    private Empresa criarEmpresa(String documentNumber,
                                 String razaoSocial,
                                 String nomeFantasia,
                                 String telefone,
                                 String cep,
                                 String endereco,
                                 String cidade,
                                 String estado,
                                 StatusEmpresa status) {

        Empresa empresa = new Empresa();
        empresa.setDocumentNumber(documentNumber);
        empresa.setRazaoSocial(razaoSocial);
        empresa.setNomeFantasia(nomeFantasia);
        empresa.setTelefone(telefone);
        empresa.setCep(cep);
        empresa.setEndereco(endereco);
        empresa.setCidade(cidade);
        empresa.setEstado(estado);
        empresa.setStatus(status);
        empresa.setDataCriacao(LocalDateTime.now());
        empresa.setDataAtualizacaoStatus(LocalDateTime.now());

        return empresa;
    }
}
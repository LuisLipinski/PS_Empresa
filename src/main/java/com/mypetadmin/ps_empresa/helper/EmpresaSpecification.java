package com.mypetadmin.ps_empresa.helper;

import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.model.Empresa;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class EmpresaSpecification {
    private EmpresaSpecification() {
        // Utility class
    }

    public static Specification<Empresa> hasDocumentNumber(String documentNumber) {
        return (root, query, cb) -> documentNumber == null ? null : cb.equal(root.get("documentNumber"), documentNumber);
    }

    public static Specification<Empresa> hasRazaoSocial(String razaoSocial) {
        return (root, query, cb) -> razaoSocial == null ? null : cb.like(cb.lower(root.get("razaoSocial")), "%" + razaoSocial.toLowerCase() + "%");
    }
    public static Specification<Empresa> hasStatus(StatusEmpresa status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Empresa> hasNomeFantasia(String nomeFantasia) {
        return (root, query, cb) -> nomeFantasia == null ? null : cb.like(cb.lower(root.get("nomeFantasia")), "%" + nomeFantasia.toLowerCase() + "%");
    }

    public static Specification<Empresa> hasCep(String cep) {
        return (root, query, cb) -> cep == null ? null : cb.equal(root.get("cep"), cep);
    }

    public static Specification<Empresa> hasEndereco(String endereco) {
        return (root, query, cb) -> endereco == null ? null : cb.like(cb.lower(root.get("endereco")), "%" + endereco.toLowerCase() + "%");
    }

    public static Specification<Empresa> hasCidade(String cidade) {
        return (root, query, cb) -> cidade == null ? null : cb.like(cb.lower(root.get("cidade")), "%" + cidade.toLowerCase() + "%");
    }

    public static Specification<Empresa> hasEstado(String estado) {
        return (root, query, cb) -> estado == null ? null : cb.equal(root.get("estado"), estado);
    }

    public static Specification<Empresa> hasTelefone(String telefone) {
        return (root, query, cb) -> telefone == null ? null : cb.equal(root.get("telefone"), telefone);
    }

    public static Specification<Empresa> hasDataAtualizacaoStatusAfter(LocalDateTime date) {
        return (root, query, cb) -> date == null ? null : cb.greaterThanOrEqualTo(root.<LocalDateTime>get("dataAtualizacaoStatus"), date);
    }

    public static Specification<Empresa> hasDataCriacaoAfter(LocalDateTime date) {
        return (root, query, cb) -> date == null ? null : cb.greaterThanOrEqualTo(root.<LocalDateTime>get("dataCriacao"), date);
    }
}

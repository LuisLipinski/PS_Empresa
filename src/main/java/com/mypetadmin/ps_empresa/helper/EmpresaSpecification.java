package com.mypetadmin.ps_empresa.helper;

import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.model.Empresa;
import org.springframework.data.jpa.domain.Specification;

public class EmpresaSpecification {
    public static Specification<Empresa> hasDocumentNumber(String documentNumber) {
        return (root, query, cb) -> documentNumber == null ? null : cb.equal(root.get("documentNumber"), documentNumber);
    }

    public static Specification<Empresa> hasRazaoSocial(String razaoSocial) {
        return (root, query, cb) -> razaoSocial == null ? null : cb.like(cb.lower(root.get("razaoSocial")), "%" + razaoSocial.toLowerCase() + "%");
    }
    public static Specification<Empresa> hasEmail(String email) {
        return (root, query, cb) -> email == null ? null : cb.equal(root.get("email"), email);
    }
    public static Specification<Empresa> hasStatus(StatusEmpresa status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }
}

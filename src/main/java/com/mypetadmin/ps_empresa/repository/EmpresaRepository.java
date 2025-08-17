package com.mypetadmin.ps_empresa.repository;

import com.mypetadmin.ps_empresa.model.Empresa;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID>, JpaSpecificationExecutor<Empresa> {
    boolean existsByDocumentNumber(String documentNumber);

    boolean existsByEmail(String email);

//    List<Empresa> findByCnpjAndRazaoSocialAndEmailAndStatus(String cnpj, String razaoSocial, String email, String status, Sort sort);
//
//    List<Empresa> findByCnpjAndEmailAndStatus(String cnpj, String email, String status, Sort sort);
//
//    List<Empresa> findByCnpjAndStatus(String cnpj, String status, Sort sort);
//
//    List<Empresa> findByCnpj(String cnpj, Sort sort);
//
//    List<Empresa> findByRazaoSocial(String razaoSocial, Sort sort);
//
//    List<Empresa> findByEmail(String email, Sort sort);
//
//    List<Empresa> findByStatus(String status, Sort sort);
}

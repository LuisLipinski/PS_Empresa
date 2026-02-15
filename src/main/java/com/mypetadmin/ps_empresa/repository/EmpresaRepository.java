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
}

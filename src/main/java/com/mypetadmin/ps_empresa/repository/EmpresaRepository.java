package com.mypetadmin.ps_empresa.repository;

import com.mypetadmin.ps_empresa.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {
    boolean existsByDocumentNumber(String documentNumber);
}

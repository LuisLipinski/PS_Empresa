package com.mypetadmin.ps_empresa.repository;

import com.mypetadmin.ps_empresa.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID>, JpaSpecificationExecutor<Empresa> {

    boolean existsByDocumentNumber(String documentNumber);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    Optional<Empresa> findByOnboardingId(UUID onboardingId);

    @Query(value = "SELECT pg_advisory_xact_lock(hashtextextended(CAST(:onboardingId AS text), 0))", nativeQuery = true)
    void lockOnboarding(@Param("onboardingId") UUID onboardingId);
}

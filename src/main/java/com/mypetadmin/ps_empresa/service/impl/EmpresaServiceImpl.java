package com.mypetadmin.ps_empresa.service.impl;

import com.mypetadmin.ps_empresa.dto.EmpresaContratoStatusDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.dto.PageResponse;
import com.mypetadmin.ps_empresa.dto.UpdateEmpresaRequestDto;
import com.mypetadmin.ps_empresa.enums.DirectionField;
import com.mypetadmin.ps_empresa.enums.SortField;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.exception.CnpjInvalidException;
import com.mypetadmin.ps_empresa.exception.EmailExistenteException;
import com.mypetadmin.ps_empresa.exception.EmpresaExistenteException;
import com.mypetadmin.ps_empresa.exception.EmpresaNaoEncontradaException;
import com.mypetadmin.ps_empresa.helper.EmpresaSpecification;
import com.mypetadmin.ps_empresa.mapper.EmpresaMapper;
import com.mypetadmin.ps_empresa.mapper.EmpresaUpdateMapper;
import com.mypetadmin.ps_empresa.model.Empresa;
import com.mypetadmin.ps_empresa.repository.EmpresaRepository;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import com.mypetadmin.ps_empresa.util.CnpjValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpresaServiceImpl implements EmpresaService {

    private static final int MAX_PAGE_SIZE = 100;

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper mapper;

    @Override
    @Transactional
    public EmpresaResponseDTO cadastrarEmpresa(EmpresaRequestDTO dto) {
        log.debug("empresa.create requested");

        if (empresaRepository.existsByDocumentNumber(dto.getDocumentNumber())) {
            throw new EmpresaExistenteException("CNPJ já cadastrado no sistema.");
        }
        if (!CnpjValidator.isCnpjValid(dto.getDocumentNumber())) {
            throw new CnpjInvalidException("CNPJ inválido.");
        }
        if (empresaRepository.existsByEmail(dto.getEmail())) {
            throw new EmailExistenteException("Email já cadastrado no sistema, informe outro email.");
        }

        Empresa empresa = mapper.toEntity(dto);
        empresa.setStatus(StatusEmpresa.AGUARDANDO_CONTRATO);
        empresa.setDataAtualizacaoStatus(LocalDateTime.now());

        Empresa salva = empresaRepository.save(empresa);
        log.info("empresa.create success empresaId={} status={}", salva.getId(), salva.getStatus());
        return mapper.toResponseDto(salva);
    }

    @Override
    @Transactional
    public void sincronizarStatusComContrato(EmpresaContratoStatusDTO dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa não encontrada"));

        StatusEmpresa statusAnterior = empresa.getStatus();

        switch (dto.getStatusContrato()) {
            case ATIVO -> empresa.setStatus(StatusEmpresa.ATIVO);
            case AGUARDANDO_PAGAMENTO -> empresa.setStatus(StatusEmpresa.AGUARDANDO_CONTRATO);
            case PENDENTE_PAGAMENTO -> {
                // Regra de negócio: empresa que já está ATIVA permanece ATIVA.
                // Para qualquer outro status, o PS_Contrato não promove a empresa para ATIVO.
            }
            case INATIVO -> empresa.setStatus(StatusEmpresa.INATIVO);
        }

        empresa.setDataAtualizacaoStatus(LocalDateTime.now());
        empresaRepository.save(empresa);

        if (statusAnterior == empresa.getStatus()) {
            log.debug(
                    "empresa.contract-status processed empresaId={} contractStatus={} statusUnchanged={}",
                    empresa.getId(), dto.getStatusContrato(), empresa.getStatus()
            );
        } else {
            log.info(
                    "empresa.contract-status changed empresaId={} contractStatus={} previousStatus={} currentStatus={}",
                    empresa.getId(), dto.getStatusContrato(), statusAnterior, empresa.getStatus()
            );
        }
    }

    @Override
    public PageResponse<EmpresaResponseDTO> getAllEmpresaSorted(
            String documentNumber,
            String razaoSocial,
            String email,
            StatusEmpresa status,
            int page,
            int size,
            SortField sortField,
            DirectionField directionField
    ) {
        documentNumber = normalize(documentNumber);
        razaoSocial = normalize(razaoSocial);
        email = normalize(email);

        if (page < 0) {
            throw new IllegalArgumentException("page deve ser maior ou igual a zero.");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("size deve estar entre 1 e " + MAX_PAGE_SIZE + ".");
        }

        if (documentNumber != null && !CnpjValidator.isCnpjValid(documentNumber)) {
            throw new CnpjInvalidException("CNPJ informado é inválido.");
        }

        Sort sort = Sort.by(
                Sort.Direction.fromString(directionField.getDirectionField()),
                sortField.getSortField()
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Empresa> spec = EmpresaSpecification.hasDocumentNumber(documentNumber)
                .and(EmpresaSpecification.hasRazaoSocial(razaoSocial))
                .and(EmpresaSpecification.hasEmail(email))
                .and(EmpresaSpecification.hasStatus(status));

        Page<EmpresaResponseDTO> pageDto = empresaRepository.findAll(spec, pageable).map(mapper::toResponseDto);

        log.debug(
                "empresa.search success page={} size={} total={} filteredByDocument={} filteredByRazaoSocial={} filteredByEmail={} filteredByStatus={}",
                pageDto.getNumber(),
                pageDto.getSize(),
                pageDto.getTotalElements(),
                documentNumber != null,
                razaoSocial != null,
                email != null,
                status != null
        );

        return new PageResponse<>(
                pageDto.getContent(),
                pageDto.getNumber(),
                pageDto.getSize(),
                pageDto.getTotalElements(),
                pageDto.getTotalPages()
        );
    }

    @Override
    public EmpresaResponseDTO getEmpresaById(UUID id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa não encontrada com o id: " + id));
        log.debug("empresa.get success empresaId={} status={}", empresa.getId(), empresa.getStatus());
        return mapper.toResponseDto(empresa);
    }

    @Override
    @Transactional
    public void deleteEmpresaById(UUID id) {
        log.debug("empresa.delete requested empresaId={}", id);
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa não encontrada com o id: " + id));
        empresaRepository.delete(empresa);
        log.info("empresa.delete success empresaId={}", id);
    }

    @Override
    @Transactional
    public EmpresaResponseDTO editEmpresaById(UUID empresaId, UpdateEmpresaRequestDto updateEmpresa) {
        log.debug("empresa.update requested empresaId={}", empresaId);
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa não encontrada com o id: " + empresaId));

        if (updateEmpresa.getEmail() != null
                && empresaRepository.existsByEmailAndIdNot(updateEmpresa.getEmail(), empresaId)) {
            throw new EmailExistenteException("Email já cadastrado no sistema, informe outro email.");
        }

        EmpresaUpdateMapper.updateEntityFromDto(empresa, updateEmpresa);
        Empresa empresaAtualizada = empresaRepository.save(empresa);
        log.info("empresa.update success empresaId={}", empresaId);
        return mapper.toResponseDto(empresaAtualizada);
    }

    private String normalize(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}

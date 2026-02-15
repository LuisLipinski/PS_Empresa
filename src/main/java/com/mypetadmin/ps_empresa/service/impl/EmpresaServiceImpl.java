package com.mypetadmin.ps_empresa.service.impl;

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
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper mapper;

    @Override
    public EmpresaResponseDTO cadastrarEmpresa(EmpresaRequestDTO dto) {
        log.debug("Iniciando cadastro da empresa com CNPJ: {}", dto.getDocumentNumber());

        if (empresaRepository.existsByDocumentNumber(dto.getDocumentNumber())) {
            log.warn("Tentativa de cadastro com CNPJ já existente: {}", dto.getDocumentNumber());
            throw new EmpresaExistenteException("CNPJ já cadastrado no sistema.");
        }
        if (!CnpjValidator.isCnpjValid(dto.getDocumentNumber())) {
            log.warn("CNPJ inválido detectado: {}", dto.getDocumentNumber());
            throw new CnpjInvalidException("CNPJ inválido.");
        }

        if (empresaRepository.existsByEmail(dto.getEmail())) {
            log.warn("Tentativa de cadastro com email já existente: {}", dto.getEmail());
            throw new EmailExistenteException("Email já cadastrado no sistema, informe outro email.");
        }
        Empresa empresa = mapper.toEntity(dto);
        empresa.setStatus(StatusEmpresa.AGUARDANDO_PAGAMENTO);
        Empresa salva = empresaRepository.save(empresa);
        log.info("Empresa salva no banco com ID: {}", salva.getId());
        return mapper.toResponseDto(salva);
    }

    @Override
    @Transactional
    public void atualizarStatus(UUID empresaId, StatusEmpresa novoStatus) {

        Objects.requireNonNull(novoStatus, "Novo status não pode ser nulo");
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa não encontrada"));

        if (!empresa.getStatus().equals(novoStatus)) {
            empresa.setStatus(novoStatus);
            empresa.setDataAtualizacaoStatus(LocalDateTime.now());
            log.info("Status da empresa {} atualizado para {}", empresaId, novoStatus);
        } else {
            log.warn("O status da empresa {} já está definido como {}", empresaId, novoStatus);
        }
    }

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
        Sort sort = Sort.by(Sort.Direction.fromString(directionField.getDirectionField()), sortField.getSortField());

        Pageable pageable = PageRequest.of(page, size, sort);

        if (documentNumber != null) {
            if (!CnpjValidator.isCnpjValid(documentNumber)) {
                throw new CnpjInvalidException("Cnpj informado é invalido");
            }
        }
        Specification<Empresa> spec = EmpresaSpecification.hasDocumentNumber(documentNumber)
                .and(EmpresaSpecification.hasRazaoSocial(razaoSocial))
                .and(EmpresaSpecification.hasEmail(email))
                .and(EmpresaSpecification.hasStatus(status));
        Page<Empresa> empresas = empresaRepository.findAll(spec, pageable);
        Page<EmpresaResponseDTO> pageDto =
                empresas.map(mapper::toResponseDto);

        StringBuilder logMessage = new StringBuilder("Busca de empresas realizada com filtros: ");
        if (documentNumber != null) logMessage.append("CNPJ=").append(documentNumber).append(" ");
        if (razaoSocial != null) logMessage.append("Razão Social=").append(razaoSocial).append(" ");
        if (email != null) logMessage.append("Email=").append(email).append(" ");
        if (status != null) logMessage.append("Status=").append(status).append(" ");
        logMessage.append("| Total resultados: ").append(empresas.getTotalElements());

        log.info(logMessage.toString());

        long totalElements = pageDto.getTotalElements();
        size = pageDto.getSize();

        int totalPages = totalElements == 0
                ? 0
                : (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(
                pageDto.getContent(),
                pageDto.getNumber(),
                size,
                totalElements,
                totalPages
        );


    }

    private String normalize(String value) {
        return (value == null || value.trim().isEmpty() ? null : value.trim());
    }

    public EmpresaResponseDTO getEmpresaById(UUID id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa não encontrada com o id: " + id));
        log.info("Empresa com o id {} foi encontrada com sucesso", id);
        return mapper.toResponseDto(empresa);
    }

    public void deleteEmpresaById(UUID id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa não encontrada com o id: " + id));
        empresaRepository.delete(empresa);
        log.info("Empresa com o id {} foi excluida com sucesso", id);
    }
    @Override
    @Transactional
    public EmpresaResponseDTO editEmpresaById(UUID empresaId, UpdateEmpresaRequestDto updateEmpresa) {
        log.info("buscando a empresa com o id " + empresaId);
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa não encontrada com o id: " + empresaId));
        try {
            log.info("Atualizando a empresa {} com os dados fornecidos", empresaId);

            EmpresaUpdateMapper.updateEntityFromDto(empresa, updateEmpresa);

            log.info("Salvando os dados no banco de dados");
            Empresa empresaAtualizada = empresaRepository.save(empresa);
            log.info("Empresa {} atualizada com sucesso.", empresaId);

            return mapper.toResponseDto(empresaAtualizada);
        } catch (Exception e) {
            log.error("Falha ao editar a empresa: {}", e.getMessage());
            throw e;
        }
    }
}

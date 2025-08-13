package com.mypetadmin.ps_empresa.service.impl;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import com.mypetadmin.ps_empresa.exception.EmailExistenteException;
import com.mypetadmin.ps_empresa.exception.EmpresaExistenteException;
import com.mypetadmin.ps_empresa.exception.EmpresaNaoEncontradaException;
import com.mypetadmin.ps_empresa.mapper.EmpresaMapper;
import com.mypetadmin.ps_empresa.model.Empresa;
import com.mypetadmin.ps_empresa.repository.EmpresaRepository;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import com.mypetadmin.ps_empresa.util.CnpjValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.UUID;

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
            throw new IllegalArgumentException("CNPJ inválido.");
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
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa não encontrada"));

        if (!empresa.getStatus().equals(novoStatus)) {
            empresa.setStatus(novoStatus);
            empresa.setDataAtualizacaoStatus(LocalDateTime.now());
            empresaRepository.save(empresa);
        } else {
            log.warn("Esse status ja esta cadastrado nesse usuário");
//            throw new MethodArgumentNotValidException(novoStatus, "O Status não pode ser o mesmo que ja esta na empresa");
        }
    }
}

package com.mypetadmin.ps_empresa.service.impl;

import com.mypetadmin.ps_empresa.dto.EmpresaRequestDTO;
import com.mypetadmin.ps_empresa.dto.EmpresaResponseDTO;
import com.mypetadmin.ps_empresa.exception.EmpresaExistenteException;
import com.mypetadmin.ps_empresa.mapper.EmpresaMapper;
import com.mypetadmin.ps_empresa.model.Empresa;
import com.mypetadmin.ps_empresa.repository.EmpresaRepository;
import com.mypetadmin.ps_empresa.service.EmpresaService;
import com.mypetadmin.ps_empresa.util.CnpjValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        Empresa empresa = mapper.toEntity(dto);
        empresa.setStatus("ATIVO");
        Empresa salva =empresaRepository.save(empresa);
        log.info("Empresa salva no banco com ID: {}", salva.getId());
        return mapper.toResponseDto(salva);
    }
}

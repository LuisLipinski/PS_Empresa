package com.mypetadmin.ps_empresa.mapper;

import com.mypetadmin.ps_empresa.dto.UpdateEmpresaRequestDto;
import com.mypetadmin.ps_empresa.helper.UpdateHelper;
import com.mypetadmin.ps_empresa.model.Empresa;

import static com.mypetadmin.ps_empresa.helper.EnderecoHelper.*;

public class EmpresaUpdateMapper {

    public static void updateEntityFromDto(Empresa empresa, UpdateEmpresaRequestDto dto) {
        UpdateHelper.applyUpdates(empresa::getNomeFantasia, dto.getNomeFantasia(), empresa::setNomeFantasia);
        UpdateHelper.applyUpdates(empresa::getTelefone, dto.getTelefone(), empresa::setTelefone);
        UpdateHelper.applyUpdates(empresa::getEmail, dto.getEmail(), empresa::setEmail);
        UpdateHelper.applyUpdates(empresa::getNomeTitular, dto.getNomeTitular(), empresa::setNomeTitular);
        UpdateHelper.applyUpdates(empresa::getCep, dto.getCep(), empresa::setCep);
        UpdateHelper.applyUpdates(empresa::getCidade, dto.getCidade(), empresa::setCidade);
        UpdateHelper.applyUpdates(empresa::getEstado, dto.getEstado(), empresa::setEstado);

        String rua = dto.getRua() != null ? dto.getRua() : extrairRua(empresa.getEndereco());
        String numero = dto.getNumero() != null ? dto.getNumero() : extrairNumero(empresa.getEndereco());
        String complemento = dto.getComplemento() != null ? dto.getComplemento() : extrairComplemento(empresa.getEndereco());
        String bairro = dto.getBairro() != null ? dto.getBairro() : extrairBairro(empresa.getEndereco());

        String enderecoAtualizado = String.format("%s, %s%s%s",
                rua,
                numero,
                (complemento != null && !complemento.isEmpty() ? " - " + complemento + ", " : ", "),
                bairro
        );
        UpdateHelper.applyUpdates(empresa::getEndereco, enderecoAtualizado, empresa::setEndereco);
    }
}

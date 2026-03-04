package com.mypetadmin.ps_empresa.enums;

public enum StatusEmpresa {
    ATIVO("Ativo"),
    AGUARDANDO_CONTRATO("Aguardando contrato"),
    INATIVO("Inativo");

    private final String descricao;

    StatusEmpresa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

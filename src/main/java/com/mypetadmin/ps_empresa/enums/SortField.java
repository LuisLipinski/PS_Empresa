package com.mypetadmin.ps_empresa.enums;

public enum SortField {
    DOCUMENT_NUMBER("documentNumber"),
    ID("id"),
    RAZAO_SOCIAL("razaoSocial"),
    STATUS("status");

    private final String sortField;

    SortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortField() {
        return sortField;
    }
}

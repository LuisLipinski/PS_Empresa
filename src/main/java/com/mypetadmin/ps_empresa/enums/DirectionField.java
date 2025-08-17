package com.mypetadmin.ps_empresa.enums;

public enum DirectionField {
    ASC("asc"),
    DESC("desc");

    private final String directionField;

    DirectionField(String directionField) {
        this.directionField = directionField;
    }

    public String getDirectionField() {
        return directionField;
    }
}

package com.mypetadmin.ps_empresa.exception;

public class TenantAccessDeniedException extends RuntimeException {

    public TenantAccessDeniedException(String message) {
        super(message);
    }
}

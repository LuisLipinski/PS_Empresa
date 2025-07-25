package com.mypetadmin.ps_empresa.exception;

public class EmpresaExistenteException extends RuntimeException{
    public EmpresaExistenteException(String mensagem) {
        super(mensagem);
    }
}

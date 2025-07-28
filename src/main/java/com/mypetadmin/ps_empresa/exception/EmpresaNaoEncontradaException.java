package com.mypetadmin.ps_empresa.exception;

public class EmpresaNaoEncontradaException extends RuntimeException{
    public EmpresaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}

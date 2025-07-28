package com.mypetadmin.ps_empresa.exception;


public class EmailExistenteException extends RuntimeException{
    public EmailExistenteException(String mensagem) {
        super(mensagem);
    }
}

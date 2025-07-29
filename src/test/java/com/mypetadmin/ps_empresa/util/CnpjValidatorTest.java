package com.mypetadmin.ps_empresa.util;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CnpjValidatorTest {
    @Test
    void deveRetornarTrueParaCnpjValido() {
        String cnpjValido = "46017245000122";
        assertTrue(CnpjValidator.isCnpjValid(cnpjValido));
    }

    @Test
    void deveRetornarFalseParaCnpjComMenosDe14Digitos() {
        String cnpjCurto = "460172450001";
        assertFalse(CnpjValidator.isCnpjValid(cnpjCurto));
    }

    @Test
    void deveRetornarFalseParaCnpjComCaracteresInvalidos() {
        String cnpjComLetras = "46017245000abc";
        assertFalse(CnpjValidator.isCnpjValid(cnpjComLetras));
    }

    @Test
    void deveRetornarFalseParaCnpjNulo() {
        assertFalse(CnpjValidator.isCnpjValid(null));
    }

    void deveRetornarFalseParaCnpjComCaracteresEspeciais() {
        String cnpjComCaracteresEspeciais = "46017245000$%@";
        assertFalse(CnpjValidator.isCnpjValid(cnpjComCaracteresEspeciais));
    }

    void deveRetornarFalseComCnpjComMaisDe14Caracteres() {
        String cnpjComMaisCaracteres = "460172450001222";
        assertFalse(CnpjValidator.isCnpjValid(cnpjComMaisCaracteres));
    }

    void deveRetornarFalseQuandoO13DigitoInvalido() {
        String cnpjInvalido = "46017245000152";
        assertFalse(CnpjValidator.isCnpjValid(cnpjInvalido));
    }

    void deveRetornarFalseQuandoO14DigitoInvalido() {
        String cnpjInvalido = "46017245000125";
        assertFalse(CnpjValidator.isCnpjValid(cnpjInvalido));
    }

    void deveRetornarFalseQuandoOCampoVazio() {
        String cnpjVazio = "";
        assertFalse(CnpjValidator.isCnpjValid(cnpjVazio));
    }
}

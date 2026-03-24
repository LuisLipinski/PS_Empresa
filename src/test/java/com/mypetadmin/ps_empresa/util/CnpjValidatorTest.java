package com.mypetadmin.ps_empresa.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CnpjValidatorTest {

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

    @Test
    void deveRetornarFalseParaCnpjComCaracteresEspeciais() {
        String cnpjComCaracteresEspeciais = "46017245000$%@";

        assertFalse(CnpjValidator.isCnpjValid(cnpjComCaracteresEspeciais));
    }

    @Test
    void deveRetornarFalseParaCnpjComMaisDe14Caracteres() {
        String cnpjComMaisCaracteres = "460172450001222";

        assertFalse(CnpjValidator.isCnpjValid(cnpjComMaisCaracteres));
    }

    @Test
    void deveRetornarFalseQuandoPrimeiroDigitoVerificadorForInvalido() {
        String cnpjInvalido = "46017245000152";

        assertFalse(CnpjValidator.isCnpjValid(cnpjInvalido));
    }

    @Test
    void deveRetornarFalseQuandoSegundoDigitoVerificadorForInvalido() {
        String cnpjInvalido = "46017245000125";

        assertFalse(CnpjValidator.isCnpjValid(cnpjInvalido));
    }

    @Test
    void deveRetornarFalseQuandoCampoForVazio() {
        String cnpjVazio = "";

        assertFalse(CnpjValidator.isCnpjValid(cnpjVazio));
    }
}
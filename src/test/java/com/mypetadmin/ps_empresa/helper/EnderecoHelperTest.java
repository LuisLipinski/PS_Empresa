package com.mypetadmin.ps_empresa.helper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnderecoHelperTest {

    @Test
    void extrairRua_quandoEnderecoCompleto_retornaRua() {
        String endereco = "Rua das Flores, 123 - Apto 45, Centro";
        String rua = EnderecoHelper.extrairRua(endereco);
        assertThat(rua).isEqualTo("Rua das Flores");
    }

    @Test
    void extrairRua_quandoEnderecoNulo_retornaNull() {
        assertThat(EnderecoHelper.extrairRua(null)).isNull();
    }

    @Test
    void extrairNumero_quandoEnderecoCompleto_retornaNumero() {
        String endereco = "Rua das Flores, 123 - Apto 45, Centro";
        String numero = EnderecoHelper.extrairNumero(endereco);
        assertThat(numero).isEqualTo("123");
    }

    @Test
    void extrairNumero_quandoEnderecoSemComplemento_retornaNumero() {
        String endereco = "Rua das Flores, 123, Centro";
        String numero = EnderecoHelper.extrairNumero(endereco);
        assertThat(numero).isEqualTo("123");
    }

    @Test
    void extrairNumero_quandoEnderecoNulo_retornaNull() {
        assertThat(EnderecoHelper.extrairNumero(null)).isNull();
    }

    @Test
    void extrairComplemento_quandoEnderecoComComplemento_retornaComplemento() {
        String endereco = "Rua das Flores, 123 - Apto 45, Centro";
        String complemento = EnderecoHelper.extrairComplemento(endereco);
        assertThat(complemento).isEqualTo("Apto 45");
    }

    @Test
    void extrairComplemento_quandoEnderecoSemComplemento_retornaNull() {
        String endereco = "Rua das Flores, 123, Centro";
        String complemento = EnderecoHelper.extrairComplemento(endereco);
        assertThat(complemento).isNull();
    }

    @Test
    void extrairComplemento_quandoEnderecoNulo_retornaNull() {
        assertThat(EnderecoHelper.extrairComplemento(null)).isNull();
    }

    @Test
    void extrairBairro_quandoEnderecoCompleto_retornaBairro() {
        String endereco = "Rua das Flores, 123 - Apto 45, Centro";
        String bairro = EnderecoHelper.extrairBairro(endereco);
        assertThat(bairro).isEqualTo("Centro");
    }

    @Test
    void extrairBairro_quandoEnderecoSemVirgula_retornaNull() {
        String endereco = "Rua das Flores 123 - Apto 45";
        String bairro = EnderecoHelper.extrairBairro(endereco);
        assertThat(bairro).isNull();
    }

    @Test
    void extrairBairro_quandoEnderecoNulo_retornaNull() {
        assertThat(EnderecoHelper.extrairBairro(null)).isNull();
    }


}

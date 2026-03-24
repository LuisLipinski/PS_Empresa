package com.mypetadmin.ps_empresa.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionControllerTest {

    @Test
    void deveRetornarValorDaVariavelDeAmbienteOuNullQuandoNaoDefinida() {
        VersionController controller = new VersionController();

        String resultado = controller.version();
        String esperado = System.getenv("RENDER_GIT_COMMIT");

        assertThat(resultado).isEqualTo(esperado);
    }
}
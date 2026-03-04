package com.mypetadmin.ps_empresa.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionControllerTest {

    @Test
    void deveExecutarSemErro() {
        VersionController controller = new VersionController();
        controller.version(); // se não lançar exceção, passou
    }
}

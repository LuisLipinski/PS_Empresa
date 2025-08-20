package com.mypetadmin.ps_empresa.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateHelperTest {
    private AtomicReference<String> stringField;
    private AtomicReference<Integer> intField;

    @BeforeEach
    void setUp() {
        stringField = new AtomicReference<>("original");
        intField = new AtomicReference<>(10);
    }

    @Test
    void updateIfNotBlank_quandoValorNaoBranco_atualizaCampo() {
        UpdateHelper.updateIfNotBlank("novo", stringField::set);
        assertThat(stringField.get()).isEqualTo("novo");
    }

    @Test
    void updateIfNotBlank_quandoValorNulo_naoAtualizaCampo() {
        UpdateHelper.updateIfNotBlank(null, stringField::set);
        assertThat(stringField.get()).isEqualTo("original");
    }

    @Test
    void updateIfNotBlank_quandoValorVazio_naoAtualizaCampo() {
        UpdateHelper.updateIfNotBlank("   ", stringField::set);
        assertThat(stringField.get()).isEqualTo("original");
    }

    @Test
    void updateIfNotNull_quandoValorNaoNulo_atualizaCampo() {
        UpdateHelper.updateIfNotNull(20, intField::set);
        assertThat(intField.get()).isEqualTo(20);
    }

    @Test
    void updateIfNotNull_quandoValorNulo_naoAtualizaCampo() {
        UpdateHelper.updateIfNotNull(null, intField::set);
        assertThat(intField.get()).isEqualTo(10);
    }

    @Test
    void updateIfChanged_quandoValorMudou_atualizaCampo() {
        Supplier<String> getter = stringField::get;
        UpdateHelper.updateIfChanged(getter, "novo", stringField::set);
        assertThat(stringField.get()).isEqualTo("novo");
    }

    @Test
    void updateIfChanged_quandoValorIgual_naoAtualizaCampo() {
        Supplier<String> getter = stringField::get;
        UpdateHelper.updateIfChanged(getter, "original", stringField::set);
        assertThat(stringField.get()).isEqualTo("original");
    }

    @Test
    void updateIfChanged_quandoValorNulo_naoAtualizaCampo() {
        Supplier<String> getter = stringField::get;
        UpdateHelper.updateIfChanged(getter, null, stringField::set);
        assertThat(stringField.get()).isEqualTo("original");
    }

    @Test
    void applyUpdates_quandoValorValidoEDiferente_atualizaCampo() {
        Supplier<String> getter = stringField::get;
        UpdateHelper.applyUpdates(getter, "novo", stringField::set);
        assertThat(stringField.get()).isEqualTo("novo");
    }

    @Test
    void applyUpdates_quandoValorNulo_naoAtualizaCampo() {
        Supplier<String> getter = stringField::get;
        UpdateHelper.applyUpdates(getter, null, stringField::set);
        assertThat(stringField.get()).isEqualTo("original");
    }

    @Test
    void applyUpdates_quandoValorVazio_naoAtualizaCampo() {
        Supplier<String> getter = stringField::get;
        UpdateHelper.applyUpdates(getter, "   ", stringField::set);
        assertThat(stringField.get()).isEqualTo("original");
    }

    @Test
    void applyUpdates_quandoValorIgual_naoAtualizaCampo() {
        Supplier<String> getter = stringField::get;
        UpdateHelper.applyUpdates(getter, "original", stringField::set);
        assertThat(stringField.get()).isEqualTo("original");
    }
}

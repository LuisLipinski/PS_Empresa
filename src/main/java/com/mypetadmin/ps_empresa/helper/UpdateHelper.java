package com.mypetadmin.ps_empresa.helper;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class UpdateHelper {

    public static void updateIfNotBlank(String newValue, Consumer<String> setter) {
        if (newValue != null && !newValue.trim().isEmpty()) {
            setter.accept(newValue);
        }
    }

    public static <T> void updateIfNotNull(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }

    public static <T> void updateIfChanged(Supplier<T> getter, T newValue, Consumer<T> setter) {
        if (newValue != null && !newValue.equals(getter.get())) {
            setter.accept(newValue);
        }
    }

    public static void applyUpdates(Supplier<String> getter, String newValue, Consumer<String> setter) {
        if (newValue != null && !newValue.trim().isEmpty() && !newValue.equals(getter.get())) {
            setter.accept(newValue);
        }
    }
}

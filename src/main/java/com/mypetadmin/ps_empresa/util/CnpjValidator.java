package com.mypetadmin.ps_empresa.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CnpjValidator {

    public static boolean isCnpjValid(String cnpj) {
        log.debug("Validando CNPJ: {}", cnpj);
        if (cnpj == null || !cnpj.matches("\\d{14}")) {
            return false;
        }

        int[] weight1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weight2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        try {
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                sum += Character.getNumericValue(cnpj.charAt(i)) * weight1[i];
            }
            int mod = sum % 11;
            int digit13 = (mod < 2) ? 0 : 11 - mod;

            if (digit13 != Character.getNumericValue(cnpj.charAt(12))) {
                return false;
            }

            sum = 0;
            for (int i = 0; i < 13; i++) {
                sum += Character.getNumericValue(cnpj.charAt(i)) * weight2[i];
            }

            mod = sum % 11;
            int digit14 = (mod < 2) ? 0 : 11 - mod;

            return digit14 == Character.getNumericValue(cnpj.charAt(13));
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

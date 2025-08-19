package com.mypetadmin.ps_empresa.helper;

public class EnderecoHelper {

    public static String extrairRua(String endereco) {
        if (endereco == null) return null;
        String[] partes = endereco.split(",", 2);
        return partes.length > 0 ? partes[0].trim() : null;
    }

    public static String extrairNumero(String endereco) {
        if (endereco == null) return null;
        String[] partes = endereco.split(",", 2);
        if (partes.length < 2) return null;

        String numeroParte = partes[1].trim();
        if (numeroParte.contains(" - ")) {
            return numeroParte.substring(0, numeroParte.indexOf(" - ")).trim();
        } else if (numeroParte.contains(",")) {
            return numeroParte.substring(0, numeroParte.indexOf(",")).trim();
        }
        return numeroParte;
    }

    public static String extrairComplemento(String endereco) {
        if (endereco == null) return null;
        if (!endereco.contains(" - ")) return null;

        String[] partes = endereco.split(" - ", 2);
        if (partes.length < 2) return null;

        String complementoParte = partes[1];
        if (complementoParte.contains(",")) {
            return complementoParte.substring(0, complementoParte.indexOf(",")).trim();
        }
        return complementoParte.trim();
    }

    public static String extrairBairro(String endereco) {
        if (endereco == null) return null;
        if (!endereco.contains(",")) return null;

        String[] partes = endereco.split(",");
        return partes[partes.length - 1].trim();
    }
}

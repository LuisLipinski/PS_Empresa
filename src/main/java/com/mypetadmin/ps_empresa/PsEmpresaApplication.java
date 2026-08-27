package com.mypetadmin.ps_empresa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class PsEmpresaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PsEmpresaApplication.class, args);
    }
}

package com.mypetadmin.ps_empresa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.mypetadmin.ps_empresa.cliente")
public class PsEmpresaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PsEmpresaApplication.class, args);
	}

}

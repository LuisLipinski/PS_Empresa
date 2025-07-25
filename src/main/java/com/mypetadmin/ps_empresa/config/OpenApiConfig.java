package com.mypetadmin.ps_empresa.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("My Pet Admin - PS_Empresa")
                        .version("v1")
                        .description("MS responsavel pela gestão de empresas do sistema My Pet Admin.")
                        .contact(new Contact().name("Luis Henrique Lipinski").email("lhlipinski@gmail.com"))
                        .contact(new Contact().name("Maria Nair Pereira Lipinski").email("mrnair.pereira@gmail.com")));
    }
}

package com.mypetadmin.ps_empresa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "EmpresaUpdate", description = "Atualização parcial dos dados cadastrais da empresa")
public class UpdateEmpresaRequestDto {

    @Size(min = 2, max = 120)
    private String nomeFantasia;

    @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter DDD e 10 ou 11 dígitos.")
    private String telefone;

    @Email(message = "Email inválido.")
    @Size(max = 254)
    private String email;

    @Size(min = 3, max = 120)
    @Pattern(
            regexp = "^[\\p{L}][\\p{L} .'-]*$",
            message = "O nome do titular deve conter apenas letras e caracteres usuais de nomes."
    )
    private String nomeTitular;

    @Size(min = 2, max = 120)
    private String rua;

    @Size(max = 20)
    @Pattern(regexp = "^[A-Za-z0-9./\\-]+$", message = "Número do imóvel inválido.")
    private String numero;

    @Size(max = 60)
    private String complemento;

    @Size(min = 2, max = 80)
    private String bairro;

    @Size(min = 2, max = 80)
    private String cidade;

    @Pattern(regexp = "^[A-Za-z]{2}$", message = "O estado deve ser informado com a UF de 2 letras.")
    private String estado;

    @Pattern(regexp = "\\d{8}", message = "O CEP deve conter exatamente 8 dígitos.")
    private String cep;
}

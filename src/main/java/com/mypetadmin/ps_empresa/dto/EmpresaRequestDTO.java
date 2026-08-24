package com.mypetadmin.ps_empresa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "Empresa", description = "Representação de uma empresa no sistema")
public class EmpresaRequestDTO {

    @NotBlank
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter exatamente 14 dígitos.")
    @Schema(description = "CNPJ da empresa sem formatação", example = "17395568000151")
    private String documentNumber;

    @NotBlank
    @Size(min = 2, max = 120)
    @Schema(description = "Razão social da empresa", example = "Pet & Cia LTDA")
    private String razaoSocial;

    @NotBlank
    @Size(min = 2, max = 120)
    @Schema(description = "Nome fantasia da empresa", example = "Pet & Cia")
    private String nomeFantasia;

    @NotBlank
    @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter DDD e 10 ou 11 dígitos.")
    @Schema(description = "Telefone com DDD, somente números", example = "41999999999")
    private String telefone;

    @NotBlank
    @Email(message = "Email inválido.")
    @Size(max = 254)
    @Schema(description = "Email comercial da empresa", example = "contato@petecia.com.br")
    private String email;

    @NotBlank
    @Size(min = 3, max = 120)
    @Pattern(
            regexp = "^[\\p{L}][\\p{L} .'-]*$",
            message = "O nome do titular deve conter apenas letras e caracteres usuais de nomes."
    )
    @Schema(description = "Nome completo do titular da conta", example = "João da Silva")
    private String nomeTitular;

    @NotBlank
    @Size(min = 2, max = 120)
    @Schema(description = "Logradouro", example = "Rua São José")
    private String rua;

    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^[A-Za-z0-9./\\-]+$", message = "Número do imóvel inválido.")
    @Schema(description = "Número do imóvel", example = "123")
    private String numero;

    @Size(max = 60)
    @Schema(description = "Complemento do endereço", example = "Bloco 05")
    private String complemento;

    @NotBlank
    @Size(min = 2, max = 80)
    @Schema(description = "Bairro", example = "Centro")
    private String bairro;

    @NotBlank
    @Size(min = 2, max = 80)
    @Schema(description = "Cidade", example = "Curitiba")
    private String cidade;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "O estado deve ser informado com a UF de 2 letras.")
    @Schema(description = "UF", example = "PR")
    private String estado;

    @NotBlank
    @Pattern(regexp = "\\d{8}", message = "O CEP deve conter exatamente 8 dígitos.")
    @Schema(description = "CEP sem formatação", example = "80000000")
    private String cep;
}

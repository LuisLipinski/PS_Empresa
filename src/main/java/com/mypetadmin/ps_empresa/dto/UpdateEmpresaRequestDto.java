package com.mypetadmin.ps_empresa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "edição da empresa", description = "Edita as empresas buscando pelo id")
public class UpdateEmpresaRequestDto {

    @Size(min = 4, max = 60)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ0-9 ]+$", message = "O nome fantasia não pode conter caracteres especiais.")
    @Schema(description = "Nome fantasia da empresa", example = "Exemplo Petshop Dogão")
    private String nomeFantasia;

    @Size(min = 11, max = 11)
    @Pattern(regexp = "\\d{11}", message = "O telefone deve conter apenas números e ter 11 digitos.")
    @Schema(description = "Numero do telefone da empresa com DDD", example = "41999999999")
    private String telefone;

    @Email(message = "Email inválido: deve ter ao menos 3 caracteres antes do @ e um domínio válido (ex: .com, .org)")
    @Pattern(
            regexp = "^[\\w.%+-]{3,}@[\\w.-]+\\.[a-zA-Z]{2,}$",
            message = "Email inválido: deve ter ao menos 3 caracteres antes do @ e um domínio válido (ex: .com, .org)"
    )
    @Schema(description = "Email comercial da empresa", example = "exemplo@empresa.com")
    private String email;

    @Pattern(regexp = "^([A-Za-zÀ-ÿ]{3,}) ([A-Za-zÀ-ÿ]{3,})$", message = "O nome do titular da conta não deve ter numeros " +
            "ou carcateres especiais, precisa ser nome completo (Nome e sobrenome), " +
            "e cada palavra não pode ter menos que 3 caracteres")
    @Schema(description = "Nome completo do titular da conta", example = "João Teste")
    private String nomeTitular;

    @Size(min = 3, max = 200)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ0-9 ]+$", message = "A rua não pode conter caracteres especiais.")
    @Schema(description = "Endereço da empresa", example = "Rua Exemplo")
    private String rua;

    @Pattern(regexp = "\\d+", message = "O número deve conter apenas números.")
    @Schema(description = "Número do imovel do endereço", example = "123")
    private String numero;

    @Size(max = 20)
    @Schema(description = "Complemento do endereço", example = "Bloco 05")
    private String complemento;

    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ ]+$", message = "O bairro deve conter apenas letras e espaços.")
    @Schema(description = "Bairro onde fica localizado a empresa", example = "Bairro Exemplo")
    private String bairro;

    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ ]+$", message = "A cidade deve conter apenas letras e espaços.")
    @Schema(description = "Cidade onde fica a empresa", example = "Curitiba")
    private String cidade;

    @Size(min = 2, max = 2)
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "O estado deve conter apenas letras e ter no máximo até 2 caracteres")
    @Schema(description = "Estado onde fica o comercio", example = "PR")
    private String estado;

    @Size(min = 8, max = 8)
    @Pattern(regexp = "\\d{8}", message = "O CEP deve conter apenas números e ter 8 digitos")
    @Schema(description = "CEP onde fica a empresa", example = "99999999")
    private String cep;
}

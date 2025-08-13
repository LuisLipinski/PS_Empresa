package com.mypetadmin.ps_empresa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "AtualizacaoStatusResponseDTO", description = "Resposta da atualização de status da empresa")
public class AtualizacaoStatusResponseDTO {

    @Schema(description = "Mensagem de resposta", example = "Status atualizado com sucesso")
    private String message;

    @Schema(description = "Código HTTP  da resposta", example = "200")
    private int statusCode;
}

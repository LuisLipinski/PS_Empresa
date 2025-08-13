package com.mypetadmin.ps_empresa.dto;

import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "atualização do status", description = "Atualiza os status de uma empresa entre 'Ativo', 'Inativo' e 'Aguardando Pagamento'")
public class AtualizacaoStatusRequestDTO {

    @NotNull
    @Schema(description = "ID da empresa a ser atualizada", example = "1")
    private UUID empresaId;

    @NotNull
    @Schema(description = "Novo Status da empresa", example = "ATIVO")
    private StatusEmpresa novoStatus;



}

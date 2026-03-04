package com.mypetadmin.ps_empresa.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ContratoStatusChangedEvent {

    private UUID empresaId;
    private String novoStatusContrato;
}

package com.mypetadmin.ps_empresa.cliente;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "ps-contrato", url = "http://localhost:8082/contratos")
public interface ContratoClient {

    @GetMapping("/empresa/{empresaId}/ativo")
    boolean empresaPossuiContratoAtivo(@PathVariable UUID empresaId);
}

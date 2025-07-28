package com.mypetadmin.ps_empresa.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "document_number", unique = true, nullable = false, length = 14)
    private String documentNumber;

    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;

    @Column(name = "nome_fantasia", nullable = false)
    private String nomeFantasia;

    @Column(name = "cep", nullable = false, length = 9)
    private String cep;

    @Column(name = "endereco", nullable = false)
    private String endereco;

    @Column(name = "cidade", nullable = false)
    private String cidade;

    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    @Column(name = "telefone", nullable = false)
    private String telefone;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "NomeTitular", nullable = false)
    private String nomeTitular;

    @Builder.Default
    @Column(name = "status", nullable = false)
    private String status = "PENDENTE ATIVACAO";

    @Column(name= "data_atualizacao_status")
    private LocalDateTime dataAtualizacaoStatus;

    @Builder.Default
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

}
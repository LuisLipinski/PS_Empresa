package com.mypetadmin.ps_empresa.model;

import com.mypetadmin.ps_empresa.enums.StatusEmpresa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "razao_social", nullable = false, length = 120)
    private String razaoSocial;

    @Column(name = "nome_fantasia", nullable = false, length = 120)
    private String nomeFantasia;

    @Column(name = "cep", nullable = false, length = 9)
    private String cep;

    @Column(name = "endereco", nullable = false, length = 255)
    private String endereco;

    @Column(name = "cidade", nullable = false, length = 120)
    private String cidade;

    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    @Column(name = "email", unique = true, nullable = false, length = 254)
    private String email;

    @Column(name = "nome_titular", nullable = false, length = 120)
    private String nomeTitular;

    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private StatusEmpresa status;

    @Column(name = "data_atualizacao_status")
    private LocalDateTime dataAtualizacaoStatus;

    @Builder.Default
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
}

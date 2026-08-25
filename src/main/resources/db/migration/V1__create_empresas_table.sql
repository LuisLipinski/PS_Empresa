CREATE TABLE IF NOT EXISTS empresas (
    id UUID PRIMARY KEY,
    document_number VARCHAR(14) NOT NULL,
    razao_social VARCHAR(120) NOT NULL,
    nome_fantasia VARCHAR(120) NOT NULL,
    cep VARCHAR(9) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    cidade VARCHAR(120) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(254) NOT NULL,
    nome_titular VARCHAR(120) NOT NULL,
    status VARCHAR(30) NOT NULL,
    data_atualizacao_status TIMESTAMP,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_empresas_document_number UNIQUE (document_number),
    CONSTRAINT uk_empresas_email UNIQUE (email)
);

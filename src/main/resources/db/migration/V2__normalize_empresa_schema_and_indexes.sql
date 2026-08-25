DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'empresas'
          AND column_name = 'nometitular'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'empresas'
          AND column_name = 'nome_titular'
    ) THEN
        ALTER TABLE empresas RENAME COLUMN nometitular TO nome_titular;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_empresas_status ON empresas(status);
CREATE INDEX IF NOT EXISTS idx_empresas_razao_social ON empresas(razao_social);

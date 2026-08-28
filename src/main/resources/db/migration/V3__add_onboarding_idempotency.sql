ALTER TABLE empresas
    ADD COLUMN onboarding_id UUID,
    ADD COLUMN onboarding_request_hash VARCHAR(64);

CREATE UNIQUE INDEX ux_empresas_onboarding_id
    ON empresas (onboarding_id)
    WHERE onboarding_id IS NOT NULL;

ALTER TABLE empresas
    ADD CONSTRAINT ck_empresas_onboarding_pair
    CHECK (
        (onboarding_id IS NULL AND onboarding_request_hash IS NULL)
        OR
        (onboarding_id IS NOT NULL AND onboarding_request_hash IS NOT NULL)
    );

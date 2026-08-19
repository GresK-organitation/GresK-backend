-- ADN Musical: perfil de 6 dimensiones que describe cómo un usuario vive la
-- música en directo (no streams). Tabla materializada, 1 fila por usuario,
-- recalculada semanalmente por MusicDnaRecalculationScheduler y de forma
-- inmediata/asíncrona ante ciertos triggers (ver CalculateUserMusicDnaUseCase).
-- Escrita exclusivamente por el módulo musicdna.

CREATE TABLE user_music_dna (
    id                    UUID          NOT NULL,
    user_id               UUID          NOT NULL,

    intensidad_score      DECIMAL(4,2)  NOT NULL,
    intensidad_label      VARCHAR(30)   NOT NULL,

    diversidad_score      DECIMAL(4,2)  NOT NULL,
    diversidad_label      VARCHAR(30)   NOT NULL,

    criticidad_score      DECIMAL(4,2)  NOT NULL,
    criticidad_label      VARCHAR(30)   NOT NULL,

    localismo_score       DECIMAL(4,2)  NOT NULL,
    localismo_label       VARCHAR(30)   NOT NULL,

    antiguedad_score      DECIMAL(4,2)  NOT NULL,
    antiguedad_label      VARCHAR(30)   NOT NULL,

    autenticidad_score    DECIMAL(4,2)  NOT NULL,
    autenticidad_label    VARCHAR(30)   NOT NULL,

    summary_phrase        VARCHAR(200)  NOT NULL,
    calculated_at         TIMESTAMPTZ   NOT NULL,

    CONSTRAINT pk_user_music_dna PRIMARY KEY (id),
    CONSTRAINT uq_user_music_dna_user_id UNIQUE (user_id),
    CONSTRAINT fk_user_music_dna_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,

    CONSTRAINT chk_intensidad_range   CHECK (intensidad_score   BETWEEN 0 AND 10),
    CONSTRAINT chk_diversidad_range   CHECK (diversidad_score   BETWEEN 0 AND 10),
    CONSTRAINT chk_criticidad_range   CHECK (criticidad_score   BETWEEN 0 AND 10),
    CONSTRAINT chk_localismo_range    CHECK (localismo_score    BETWEEN 0 AND 10),
    CONSTRAINT chk_antiguedad_range   CHECK (antiguedad_score   BETWEEN 0 AND 10),
    CONSTRAINT chk_autenticidad_range CHECK (autenticidad_score BETWEEN 0 AND 10)
);

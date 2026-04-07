CREATE TABLE users (
                       id              UUID            NOT NULL,
                       email           VARCHAR(255)    NOT NULL,
                       password        VARCHAR(255)    NOT NULL,
                       name            VARCHAR(255)    NOT NULL,
                       description     VARCHAR(600),
                       city            VARCHAR(255),
                       status          VARCHAR(50)     NOT NULL,
                       tier            VARCHAR(50)     NOT NULL,
                       loyalty_points  INTEGER         NOT NULL DEFAULT 0,
                       created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
                       updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
                       version         BIGINT          NOT NULL DEFAULT 0,

                       CONSTRAINT pk_users PRIMARY KEY (id),
                       CONSTRAINT uq_users_email UNIQUE (email),
                       CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DELETED')),
                       CONSTRAINT chk_users_tier   CHECK (tier   IN ('FREE', 'PREMIUM', 'VIP')),
                       CONSTRAINT chk_users_loyalty_points CHECK (loyalty_points >= 0)
);

CREATE TABLE user_roles (
                            user_id UUID        NOT NULL,
                            role    VARCHAR(50) NOT NULL,

                            CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role),
                            CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                            CONSTRAINT chk_user_roles_role CHECK (role IN ('USER', 'PROMOTER', 'ADMIN'))
);

CREATE TABLE user_genres (
                             user_id UUID        NOT NULL,
                             genre   VARCHAR(50) NOT NULL,

                             CONSTRAINT pk_user_genres PRIMARY KEY (user_id, genre),
                             CONSTRAINT fk_user_genres_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_users_email  ON users (email);
CREATE INDEX idx_users_status ON users (status);
CREATE INDEX idx_users_tier   ON users (tier);
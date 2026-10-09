-- EDABIP | Users module | schema (works on MySQL 8 and H2 in MySQL mode)
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    user_id     VARCHAR(20),
    name        VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL,
    role        VARCHAR(30)   NOT NULL,
    status      VARCHAR(10)   NOT NULL DEFAULT 'Active',
    last_login  TIMESTAMP     NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_user_id UNIQUE (user_id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_users_status CHECK (status IN ('Active', 'Inactive')),
    CONSTRAINT chk_users_role CHECK (role IN ('Administrator', 'Editor', 'Viewer', 'Business Analyst', 'User'))
);

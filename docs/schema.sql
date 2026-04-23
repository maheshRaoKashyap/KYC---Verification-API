-- ============================================================
-- KYC Platform - Complete Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS kyc_auth_db;
CREATE DATABASE IF NOT EXISTS kyc_db;

-- ─── kyc_auth_db ─────────────────────────────────────────────

USE kyc_auth_db;

CREATE TABLE IF NOT EXISTS roles (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT chk_role_name CHECK (name IN ('ROLE_USER','ROLE_ADMIN','ROLE_KYC_OFFICER'))
);

CREATE TABLE IF NOT EXISTS users (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    email            VARCHAR(255) NOT NULL UNIQUE,
    password         VARCHAR(255) NOT NULL,
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100) NOT NULL,
    phone            VARCHAR(20)  UNIQUE,
    date_of_birth    VARCHAR(20),
    gender           VARCHAR(20),
    citizenship      VARCHAR(100),
    address          TEXT,
    city             VARCHAR(100),
    state            VARCHAR(100),
    profession       VARCHAR(100),
    aadhaar_number   VARCHAR(20)  UNIQUE,
    pan_number       VARCHAR(15)  UNIQUE,
    voter_id_number  VARCHAR(30)  UNIQUE,
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED'))
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS api_credentials (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT NOT NULL UNIQUE,
    api_key      VARCHAR(100) NOT NULL UNIQUE,
    app_id       VARCHAR(60)  NOT NULL UNIQUE,
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT,
    action      VARCHAR(100) NOT NULL,
    resource    VARCHAR(100),
    ip_address  VARCHAR(50),
    user_agent  VARCHAR(500),
    status      VARCHAR(20),
    details     VARCHAR(1000),
    timestamp   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_user_id (user_id),
    INDEX idx_audit_timestamp (timestamp)
);

-- ─── Seed default roles ───────────────────────────────────────
INSERT IGNORE INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN'), ('ROLE_KYC_OFFICER');

-- ─── Default admin user (password: Admin@1234) ───────────────
INSERT IGNORE INTO users (email, password, first_name, last_name, status)
VALUES ('admin@kycplatform.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj8oqw7ZWWQ.',
        'Platform', 'Admin', 'ACTIVE');

SET @admin_id = (SELECT id FROM users WHERE email = 'admin@kycplatform.com');
SET @admin_role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN');
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (@admin_id, @admin_role_id);

-- ─── kyc_db ──────────────────────────────────────────────────

USE kyc_db;

CREATE TABLE IF NOT EXISTS kyc_details (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id               BIGINT NOT NULL UNIQUE,
    email                 VARCHAR(255) NOT NULL,
    aadhaar_number        VARCHAR(20),
    pan_number            VARCHAR(15),
    voter_id_number       VARCHAR(30),
    passport_number       VARCHAR(20),
    driving_license_number VARCHAR(30),
    full_name             VARCHAR(255) NOT NULL,
    date_of_birth         VARCHAR(20),
    gender                VARCHAR(20),
    father_name           VARCHAR(255),
    mother_name           VARCHAR(255),
    nationality           VARCHAR(100),
    permanent_address     TEXT,
    current_address       TEXT,
    pin_code              VARCHAR(10),
    district              VARCHAR(100),
    state                 VARCHAR(100) NOT NULL,
    country               VARCHAR(100) NOT NULL,
    bank_account_number   VARCHAR(30),
    ifsc_code             VARCHAR(15),
    bank_name             VARCHAR(100),
    annual_income         VARCHAR(50),
    source_of_funds       VARCHAR(100),
    aadhaar_verified      BOOLEAN NOT NULL DEFAULT FALSE,
    pan_verified          BOOLEAN NOT NULL DEFAULT FALSE,
    address_verified      BOOLEAN NOT NULL DEFAULT FALSE,
    status                VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    rejection_reason      TEXT,
    verified_by           BIGINT,
    verified_at           DATETIME,
    created_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_kyc_status CHECK (status IN ('PENDING','UNDER_REVIEW','VERIFIED','REJECTED','EXPIRED')),
    INDEX idx_kyc_user_id (user_id),
    INDEX idx_kyc_status  (status),
    INDEX idx_kyc_email   (email)
);

-- ─── Unified view for admin monitoring ────────────────────────
CREATE OR REPLACE VIEW kyc_auth_db.v_user_kyc_summary AS
SELECT
    u.id         AS user_id,
    u.email,
    u.first_name,
    u.last_name,
    u.phone,
    u.status     AS user_status,
    u.created_at AS registered_at
FROM kyc_auth_db.users u;

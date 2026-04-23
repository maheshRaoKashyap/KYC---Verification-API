-- KYC Platform Database Schema
-- Run on MySQL 8.0+

CREATE DATABASE IF NOT EXISTS kyc_auth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS kyc_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE kyc_auth_db;

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    INDEX idx_role_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    email          VARCHAR(255) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    phone          VARCHAR(20)  UNIQUE,
    date_of_birth  VARCHAR(20),
    gender         VARCHAR(20),
    citizenship    VARCHAR(50),
    address        TEXT,
    city           VARCHAR(100),
    state          VARCHAR(100),
    profession     VARCHAR(100),
    aadhaar_number VARCHAR(20)  UNIQUE,
    pan_number     VARCHAR(20)  UNIQUE,
    voter_id_number VARCHAR(30) UNIQUE,
    status         ENUM('ACTIVE','INACTIVE','SUSPENDED') DEFAULT 'ACTIVE',
    created_at     DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at     DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_email        (email),
    INDEX idx_status       (status),
    INDEX idx_pan          (pan_number),
    INDEX idx_aadhaar      (aadhaar_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User-Roles join table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB;

-- API Credentials table
CREATE TABLE IF NOT EXISTS api_credentials (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT NOT NULL UNIQUE,
    api_key      VARCHAR(100) NOT NULL UNIQUE,
    app_id       VARCHAR(60)  NOT NULL UNIQUE,
    active       TINYINT(1)   DEFAULT 1,
    created_at   DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    last_used_at DATETIME(6),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_api_key (api_key),
    INDEX idx_app_id  (app_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Audit Logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT,
    action     VARCHAR(100) NOT NULL,
    resource   VARCHAR(100),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    status     ENUM('SUCCESS','FAILURE') DEFAULT 'SUCCESS',
    details    VARCHAR(1000),
    timestamp  DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_user_id  (user_id),
    INDEX idx_action   (action),
    INDEX idx_timestamp(timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed default roles
INSERT IGNORE INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN'), ('ROLE_KYC_OFFICER');

-- Seed default admin user (password: Admin@123456)
-- BCrypt hash of 'Admin@123456' with strength 12
INSERT IGNORE INTO users
    (email, password, first_name, last_name, phone, status)
VALUES
    ('admin@kycplatform.com',
     '$2a$12$LqkGGln5HjHiWyuKr8L3NuPKEsMiF1.OkGFGFaYpfpjuq6kfOQE.m',
     'Platform', 'Admin', '9000000000', 'ACTIVE');

-- Assign ADMIN role
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@kycplatform.com' AND r.name = 'ROLE_ADMIN';

USE kyc_db;

-- KYC Details table
CREATE TABLE IF NOT EXISTS kyc_details (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id               BIGINT NOT NULL UNIQUE,
    email                 VARCHAR(255) NOT NULL,
    aadhaar_number        VARCHAR(20),
    pan_number            VARCHAR(20),
    voter_id_number       VARCHAR(30),
    passport_number       VARCHAR(20),
    driving_license_number VARCHAR(30),
    full_name             VARCHAR(200) NOT NULL,
    date_of_birth         VARCHAR(20),
    gender                VARCHAR(20),
    father_name           VARCHAR(200),
    mother_name           VARCHAR(200),
    nationality           VARCHAR(100),
    permanent_address     TEXT,
    current_address       TEXT,
    pin_code              VARCHAR(10),
    district              VARCHAR(100),
    state                 VARCHAR(100) NOT NULL,
    country               VARCHAR(100) NOT NULL,
    bank_account_number   VARCHAR(30),
    ifsc_code             VARCHAR(20),
    bank_name             VARCHAR(200),
    annual_income         VARCHAR(50),
    source_of_funds       VARCHAR(200),
    aadhaar_verified      TINYINT(1) DEFAULT 0,
    pan_verified          TINYINT(1) DEFAULT 0,
    address_verified      TINYINT(1) DEFAULT 0,
    status                ENUM('PENDING','UNDER_REVIEW','VERIFIED','REJECTED','EXPIRED') DEFAULT 'PENDING',
    rejection_reason      TEXT,
    verified_by           BIGINT,
    verified_at           DATETIME(6),
    created_at            DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at            DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_user_id     (user_id),
    INDEX idx_status      (status),
    INDEX idx_email       (email),
    INDEX idx_pan         (pan_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

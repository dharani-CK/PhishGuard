CREATE DATABASE phishguard_db;
USE phishguard_db;

-- create users
-- Stores user accounts
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    master_password_hash VARCHAR(255) NOT NULL,
    failed_login_attempts INT DEFAULT 0,
    account_locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- create credentials table
-- Stores encrypted website credentials
CREATE TABLE credentials (
    credential_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    website_url VARCHAR(255) NOT NULL,
    website_name VARCHAR(150),
    username VARCHAR(150),
    encrypted_password TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- create phisphing reports table
-- Crowd-sourced phishing reports
CREATE TABLE phishing_reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    reported_url VARCHAR(255) NOT NULL,
    reported_by INT,
    report_reason TEXT,
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (reported_by) REFERENCES users(user_id)
);

-- create a table for login logs
CREATE TABLE login_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN,
    ip_address VARCHAR(100),

    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
CREATE TABLE trusted_domains (
    id INT AUTO_INCREMENT PRIMARY KEY,
    domain VARCHAR(255) UNIQUE
);

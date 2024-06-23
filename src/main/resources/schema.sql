-- Create table for AppUser
CREATE TABLE app_user
(
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                    VARCHAR(255),
    lastname                VARCHAR(255),
    username                VARCHAR(255) UNIQUE NOT NULL,
    password                VARCHAR(255)        NOT NULL,
    account_non_expired     BOOLEAN             NOT NULL,
    account_non_locked      BOOLEAN             NOT NULL,
    credentials_non_expired BOOLEAN             NOT NULL,
    enabled                 BOOLEAN             NOT NULL,
    login_attempts          INT,
    roles                   VARCHAR(255)
);

-- Create table for Payroll
CREATE TABLE payroll
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    period VARCHAR (7) NOT NULL,
    salary      BIGINT NOT NULL,
    app_user_id BIGINT,
    FOREIGN KEY (app_user_id) REFERENCES app_user (id)
);

-- Create table for SecurityEvent
CREATE TABLE security_event
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    date    VARCHAR(255) NOT NULL,
    action  VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    object  VARCHAR(255) NOT NULL,
    path    VARCHAR(255) NOT NULL
);

-- Create table for roles (Element Collection for AppUser)
CREATE TABLE app_user_roles
(
    app_user_id BIGINT,
    roles       VARCHAR(255),
    FOREIGN KEY (app_user_id) REFERENCES app_user (id)
);

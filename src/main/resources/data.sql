-- Insert roles for AppUser with id 1
INSERT INTO app_user (name, lastname, username, password, account_non_expired, account_non_locked,
                      credentials_non_expired, enabled, login_attempts)
VALUES ('John', 'Doe', 'john.doe', 'password', true, true, true, true, 0),
       ('Jane', 'Doe', 'jane.doe', 'password', true, true, true, true, 0);

-- Insert roles for AppUser
INSERT INTO app_user_roles (app_user_id, roles)
VALUES (1, 'ROLE_USER'),
       (1, 'ROLE_ADMIN'),
       (2, 'ROLE_USER');

-- Insert Payroll data
INSERT INTO payroll (period, salary, app_user_id)
VALUES ('01-2024', 50000, 1),
       ('02-2024', 50000, 1),
       ('01-2024', 45000, 2);

-- Insert SecurityEvent data
INSERT INTO security_event (date, action, subject, object, path)
VALUES ('2024-06-22', 'LOGIN_SUCCESS', 'john.doe', 'localhost', '/login'),
       ('2024-06-22', 'LOGIN_FAILURE', 'jane.doe', 'localhost', '/login');

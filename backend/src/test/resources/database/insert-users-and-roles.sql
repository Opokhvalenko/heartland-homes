INSERT INTO users (id, email, password, first_name, last_name, is_deleted)
VALUES
    (1, 'test@example.com', 'password', 'Test', 'User', false);

INSERT INTO users_roles (users_id, roles_id) VALUES (1, 1);

INSERT INTO users (id, email, password, first_name, last_name, is_deleted)
VALUES
    (2, 'test2@example.com', 'password2', 'Test', 'User', false);

INSERT INTO users_roles (users_id, roles_id) VALUES (2, 2);

INSERT INTO users (id, email, password, first_name, last_name, is_deleted)
VALUES
    (3, 'test3@example.com', 'password3', 'Test', 'User', false);

INSERT INTO users_roles (users_id, roles_id) VALUES (3, 2);
-- Создание пользователя с ролью ADMIN
INSERT INTO users (id, username, password, active, password_encrypted, avatar_path) 
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', true, true, null);

-- Добавление роли ADMIN
INSERT INTO user_role (user_id, roles) VALUES (1, 'ADMIN');

-- Сброс последовательности
ALTER SEQUENCE hibernate_sequence RESTART WITH 2;

-- Создание тестового пользователя
-- Пароль: test123 (зашифрован BCrypt)
INSERT INTO users (username, password, active, password_encrypted) 
VALUES ('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', true, true);

-- Добавление роли USER
INSERT INTO user_role (user_id, roles) 
VALUES (1, 'USER'); 
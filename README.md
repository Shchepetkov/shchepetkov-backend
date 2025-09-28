# Spring Boot REST API

Современное REST API приложение на Spring Boot с JWT аутентификацией, Spring Security и H2 базой данных.

## 🚀 **Возможности**

- **JWT Аутентификация** - безопасная аутентификация с JSON Web Tokens
- **Spring Security** - современная конфигурация безопасности
- **REST API** - полный набор CRUD операций
- **Валидация данных** - проверка входных данных
- **Обработка ошибок** - централизованная обработка исключений
- **Логирование** - структурированное логирование
- **H2 Database** - встроенная база данных для разработки
- **File Upload** - безопасная загрузка файлов

## 🛠️ **Технологии**

- **Spring Boot 2.7.1**
- **Spring Security 5.7.2**
- **Spring Data JPA**
- **H2 Database**
- **JWT (jjwt 0.11.5)**
- **Lombok**
- **Gradle**

## 📋 **API Endpoints**

### **Аутентификация**
- `POST /api/auth/login` - Вход в систему
- `POST /api/auth/logout` - Выход из системы
- `GET /api/auth/me` - Информация о текущем пользователе
- `GET /api/auth/check` - Проверка статуса аутентификации
- `POST /api/auth/refresh` - Обновление токена

### **Регистрация**
- `POST /api/registration` - Регистрация пользователя
- `GET /api/registration/check/{username}` - Проверка доступности имени пользователя

### **Пользователи**
- `GET /api/users` - Получить всех пользователей
- `GET /api/users/{id}` - Получить пользователя по ID
- `GET /api/users/username/{username}` - Получить пользователя по имени

### **Администрирование** (требует роль ADMIN)
- `GET /api/admin/users` - Получить всех пользователей
- `GET /api/admin/stats` - Статистика пользователей
- `PUT /api/admin/users/{userId}/activate` - Активировать пользователя
- `PUT /api/admin/users/{userId}/deactivate` - Деактивировать пользователя
- `PUT /api/admin/users/{userId}/roles` - Обновить роли пользователя

### **Сообщения**
- `GET /api/messages` - Получить все сообщения
- `POST /api/messages` - Создать сообщение
- `GET /api/messages?tag={tag}` - Получить сообщения по тегу
- `DELETE /api/messages/{id}` - Удалить сообщение

### **Файлы**
- `POST /api/files/avatar` - Загрузить аватар
- `GET /api/files/avatar/{filename}` - Получить аватар
- `DELETE /api/files/avatar/{userId}` - Удалить аватар

### **Система**
- `GET /actuator/health` - Проверка здоровья приложения
- `GET /h2-console` - H2 Database Console

## 🔧 **Настройка**

### **Переменные окружения**
```bash
# JWT Configuration
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

# Database (для продакшена)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/mydb
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password

# File Upload
UPLOAD_PATH=/path/to/uploads
HOSTNAME=192.168.1.100
```

### **Запуск приложения**
```bash
# Сборка
./gradlew build

# Запуск
./gradlew bootRun

# Или через JAR
java -jar build/libs/shchepetkov-backend-1.0-SNAPSHOT.jar
```

## 🔐 **Безопасность**

- **JWT токены** для аутентификации
- **BCrypt** для хеширования паролей
- **CORS** настроен для всех источников
- **Валидация** входных данных
- **Проверка типов файлов** при загрузке
- **Ограничение размера файлов** (5MB)

## 📊 **Логирование**

Логи настраиваются через `logback-spring.xml`:
- **Development**: консольный вывод
- **Production**: файловое логирование с ротацией

## 🗄️ **База данных**

### **H2 Console**
- URL: `http://localhost:8086/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (пустое)

### **Схема базы данных**
```sql
-- Пользователи
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    active BOOLEAN,
    password_encrypted BOOLEAN,
    avatar_path VARCHAR(255)
);

-- Роли пользователей
CREATE TABLE user_role (
    user_id BIGINT,
    roles VARCHAR(255)
);

-- Сообщения
CREATE TABLE messages (
    id BIGINT PRIMARY KEY,
    text VARCHAR(255),
    tag VARCHAR(255),
    user_id BIGINT
);
```

## 🧪 **Тестирование**

### **Примеры запросов**

**Регистрация:**
```bash
curl -X POST http://localhost:8086/api/registration \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}'
```

**Вход:**
```bash
curl -X POST http://localhost:8086/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}'
```

**Получение статистики (требует токен):**
```bash
curl -X GET http://localhost:8086/api/admin/stats \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 📝 **Лицензия**

MIT License

## 👥 **Автор**

Shchepetkov Backend API

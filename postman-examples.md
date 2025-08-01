# Postman Examples with JWT

## 1. Проверка здоровья API
**Method:** GET  
**URL:** `http://localhost:8080/api/test/health`

**Expected Response:**
```json
{
  "status": "OK",
  "message": "API is working",
  "timestamp": 1703123456789
}
```

## 2. Вход в систему (Login) - Получение JWT токена
**Method:** POST  
**URL:** `http://localhost:8080/api/auth/login`  
**Headers:** `Content-Type: application/json`  
**Body (raw JSON):**
```json
{
  "username": "testuser",
  "password": "test123"
}
```

**Expected Response (Success):**
```json
{
  "success": true,
  "message": "Login successful",
  "token": "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTcwMzEyMzQ1NiwibmJmIjoxNzAzMTIzNDU2LCJleHAiOjE3MDMxMjQwNTZ9...",
  "user": {
    "id": 1,
    "username": "testuser",
    "active": true,
    "avatarPath": null
  }
}
```

**Expected Response (Error):**
```json
{
  "success": false,
  "message": "Invalid username or password"
}
```

## 3. Проверка текущего пользователя (/me) с JWT
**Method:** GET  
**URL:** `http://localhost:8080/api/auth/me`  
**Headers:** `Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...`

**Expected Response (Not Authenticated):**
```json
{
  "success": false,
  "message": "Not authenticated"
}
```

**Expected Response (Authenticated):**
```json
{
  "success": true,
  "user": {
    "id": 1,
    "username": "testuser",
    "active": true,
    "avatarPath": null
  }
}
```

## 4. Проверка статуса аутентификации
**Method:** GET  
**URL:** `http://localhost:8080/api/auth/check`

**Expected Response (Not Authenticated):**
```json
{
  "authenticated": false
}
```

**Expected Response (Authenticated):**
```json
{
  "authenticated": true,
  "username": "testuser"
}
```

## 5. Обновление JWT токена (Refresh)
**Method:** POST  
**URL:** `http://localhost:8080/api/auth/refresh`  
**Headers:** `Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...`

**Expected Response (Success):**
```json
{
  "success": true,
  "token": "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "message": "Token refreshed successfully"
}
```

**Expected Response (Error):**
```json
{
  "success": false,
  "message": "Invalid or expired token"
}
```

## 6. Выход из системы
**Method:** POST  
**URL:** `http://localhost:8080/api/auth/logout`

**Expected Response:**
```json
{
  "success": true,
  "message": "Logout successful"
}
```

## Порядок тестирования в Postman:

1. **Сначала выполните логин** (POST `/api/auth/login`) с правильными данными
2. **Скопируйте JWT токен** из ответа (поле "token")
3. **Добавьте токен в заголовок** для последующих запросов:
   - В Postman: Headers → Key: `Authorization`, Value: `Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...`
4. **Проверьте /me** (GET `/api/auth/me`) - должен вернуть данные пользователя
5. **Проверьте /check** (GET `/api/auth/check`) - должен показать, что аутентифицирован
6. **Обновите токен** (POST `/api/auth/refresh`) если нужно
7. **Выполните logout** (POST `/api/auth/logout`)

## Тестовые данные:
- **Username:** testuser
- **Password:** test123

## Настройка в Postman:

### Вариант 1: Ручное добавление токена
1. После логина скопируйте токен из ответа
2. В каждом защищённом запросе добавьте заголовок:
   - Key: `Authorization`
   - Value: `Bearer <ваш_токен>`

### Вариант 2: Автоматическое использование токена
1. Создайте переменную окружения в Postman:
   - Variable: `jwt_token`
   - Initial Value: (оставьте пустым)
2. В тесте логина добавьте скрипт:
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("jwt_token", response.token);
}
```
3. В защищённых запросах используйте заголовок:
   - Key: `Authorization`
   - Value: `Bearer {{jwt_token}}`

## Важные моменты:
- JWT токены имеют срок действия (24 часа по умолчанию)
- Токены содержат информацию о пользователе, но не хранят пароль
- Для обновления токена используйте endpoint `/api/auth/refresh`
- Все защищённые API требуют заголовок `Authorization: Bearer <token>` 
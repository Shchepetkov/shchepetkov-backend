# Тестовый скрипт для проверки API

Write-Host "=== Тестирование API ===" -ForegroundColor Green

# 1. Создание пользователя
Write-Host "1. Создание пользователя..." -ForegroundColor Yellow
$body = @{username="testuser"; password="test123"} | ConvertTo-Json
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8086/api/registration" -Method POST -Body $body -ContentType "application/json"
    Write-Host "Регистрация: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Ответ: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "Ошибка регистрации: $($_.Exception.Message)" -ForegroundColor Red
}

# 2. Вход в систему
Write-Host "`n2. Вход в систему..." -ForegroundColor Yellow
$body = @{username="testuser"; password="test123"} | ConvertTo-Json
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8086/api/auth/login" -Method POST -Body $body -ContentType "application/json"
    Write-Host "Вход: $($response.StatusCode)" -ForegroundColor Green
    $loginResponse = $response.Content | ConvertFrom-Json
    $token = $loginResponse.token
    Write-Host "Токен получен: $($token.Substring(0, 20))..." -ForegroundColor Cyan
} catch {
    Write-Host "Ошибка входа: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 3. Проверка аутентификации
Write-Host "`n3. Проверка аутентификации..." -ForegroundColor Yellow
$headers = @{Authorization=$token}
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8086/api/auth/me" -Method GET -Headers $headers
    Write-Host "Проверка auth: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Ответ: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "Ошибка проверки auth: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. Попытка доступа к admin API (должна вернуть 403)
Write-Host "`n4. Попытка доступа к admin API..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8086/api/admin/stats" -Method GET -Headers $headers
    Write-Host "Admin API: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Ответ: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "Admin API (ожидаемая ошибка 403): $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host "`n=== Testing completed ===" -ForegroundColor Green

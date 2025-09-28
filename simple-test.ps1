# Simple API Test

Write-Host "=== API Testing ===" -ForegroundColor Green

# 1. Create user
Write-Host "1. Creating user..." -ForegroundColor Yellow
$body = @{username="testuser"; password="test123"} | ConvertTo-Json
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8086/api/registration" -Method POST -Body $body -ContentType "application/json"
    Write-Host "Registration: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "Registration error: $($_.Exception.Message)" -ForegroundColor Red
}

# 2. Login
Write-Host "`n2. Login..." -ForegroundColor Yellow
$body = @{username="testuser"; password="test123"} | ConvertTo-Json
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8086/api/auth/login" -Method POST -Body $body -ContentType "application/json"
    Write-Host "Login: $($response.StatusCode)" -ForegroundColor Green
    $loginResponse = $response.Content | ConvertFrom-Json
    $token = $loginResponse.token
    Write-Host "Token received: $($token.Substring(0, 20))..." -ForegroundColor Cyan
} catch {
    Write-Host "Login error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 3. Test admin API (should return 403)
Write-Host "`n3. Testing admin API..." -ForegroundColor Yellow
$headers = @{Authorization=$token}
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8086/api/admin/stats" -Method GET -Headers $headers
    Write-Host "Admin API: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "Admin API (expected 403): $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host "`n=== Testing completed ===" -ForegroundColor Green

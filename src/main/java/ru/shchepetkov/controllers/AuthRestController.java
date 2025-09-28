package ru.shchepetkov.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.shchepetkov.models.User;
import ru.shchepetkov.service.UserService;
import ru.shchepetkov.dto.LoginRequest;
import ru.shchepetkov.dto.LoginResponse;
import ru.shchepetkov.dto.UserDto;
import ru.shchepetkov.security.JwtTokenProvider;

import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthRestController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setActive(user.isActive());
        dto.setAvatarPath(user.getAvatarPath());
        return dto;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        LoginResponse response = new LoginResponse();
        
        try {
            // Проверяем, что пользователь существует и активен
            User user = userService.findByUsernameOrThrow(request.getUsername());
            if (!user.isActive()) {
                log.warn("Login attempt for deactivated user: {}", request.getUsername());
                response.setSuccess(false);
                response.setMessage("Account is deactivated");
                return ResponseEntity.badRequest().body(response);
            }

            // Аутентифицируем пользователя
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Генерируем JWT токен
            String jwtToken = jwtTokenProvider.generateToken(authentication);
            
            // Создаём ответ
            response.setSuccess(true);
            response.setMessage("Login successful");
            response.setUser(toDto(user));
            response.setToken("Bearer " + jwtToken);
            
            log.info("Successful login for user: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("Failed login attempt for user: {}, error: {}", request.getUsername(), e.getMessage());
            response.setSuccess(false);
            response.setMessage("Invalid username or password");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        SecurityContextHolder.clearContext();
        response.put("success", true);
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Map<String, Object> response = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getName())) {
            try {
                User user = userService.findByUsernameOrThrow(authentication.getName());
                response.put("success", true);
                response.put("user", toDto(user));
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAuth() {
        Map<String, Object> response = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getName())) {
            response.put("authenticated", true);
            response.put("username", authentication.getName());
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (jwtTokenProvider.validateToken(token) && !jwtTokenProvider.isTokenExpired(token)) {
                    String username = jwtTokenProvider.getUsernameFromToken(token);
                    String newToken = jwtTokenProvider.generateToken(username);
                    
                    response.put("success", true);
                    response.put("token", "Bearer " + newToken);
                    response.put("message", "Token refreshed successfully");
                    return ResponseEntity.ok(response);
                }
            }
            
            response.put("success", false);
            response.put("message", "Invalid or expired token");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Token refresh failed");
            return ResponseEntity.badRequest().body(response);
        }
    }
} 
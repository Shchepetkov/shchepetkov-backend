package ru.shchepetkov.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shchepetkov.models.Role;
import ru.shchepetkov.models.User;
import ru.shchepetkov.service.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RegistrationRestController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        if (userService.existsByUsername(user.getUsername())) {
            response.put("success", false);
            response.put("message", "User already exists!");
            return ResponseEntity.badRequest().body(response);
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        
        boolean saved = userService.saveUser(user);
        if (saved) {
            response.put("success", true);
            response.put("message", "User registered successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("success", false);
            response.put("message", "Failed to register user");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/check/{username}")
    public ResponseEntity<Map<String, Object>> checkUsername(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = userService.existsByUsername(username);
        response.put("exists", exists);
        response.put("available", !exists);
        return ResponseEntity.ok(response);
    }
} 
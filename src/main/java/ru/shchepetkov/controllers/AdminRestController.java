package ru.shchepetkov.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.shchepetkov.models.Role;
import ru.shchepetkov.models.User;
import ru.shchepetkov.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminRestController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@AuthenticationPrincipal User admin) {
        if (admin == null || !admin.isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(userService.findAll());
    }

    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<Map<String, Object>> activateUser(
            @AuthenticationPrincipal User admin,
            @PathVariable Long userId) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (admin == null || !admin.isAdmin()) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.status(403).body(response);
        }

        try {
            userService.activateUser(userId);
            response.put("success", true);
            response.put("message", "User activated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateUser(
            @AuthenticationPrincipal User admin,
            @PathVariable Long userId) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (admin == null || !admin.isAdmin()) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.status(403).body(response);
        }

        try {
            userService.deactivateUser(userId);
            response.put("success", true);
            response.put("message", "User deactivated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<Map<String, Object>> updateUserRoles(
            @AuthenticationPrincipal User admin,
            @PathVariable Long userId,
            @RequestBody RoleUpdateRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (admin == null || !admin.isAdmin()) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.status(403).body(response);
        }

        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setRoles(request.getRoles());
                userService.save(user);
                
                response.put("success", true);
                response.put("message", "User roles updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@AuthenticationPrincipal User admin) {
        Map<String, Object> response = new HashMap<>();
        
        if (admin == null || !admin.isAdmin()) {
            response.put("success", false);
            response.put("message", "Access denied");
            return ResponseEntity.status(403).body(response);
        }

        List<User> allUsers = userService.findAll();
        long totalUsers = allUsers.size();
        long activeUsers = allUsers.stream().filter(User::isActive).count();
        long adminUsers = allUsers.stream().filter(User::isAdmin).count();

        response.put("success", true);
        response.put("totalUsers", totalUsers);
        response.put("activeUsers", activeUsers);
        response.put("adminUsers", adminUsers);
        response.put("inactiveUsers", totalUsers - activeUsers);
        
        return ResponseEntity.ok(response);
    }

    public static class RoleUpdateRequest {
        private Set<Role> roles;

        public Set<Role> getRoles() { return roles; }
        public void setRoles(Set<Role> roles) { this.roles = roles; }
    }
} 
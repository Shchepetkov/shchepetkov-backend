package ru.shchepetkov.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.shchepetkov.models.Message;
import ru.shchepetkov.models.User;
import ru.shchepetkov.service.MessageService;
import ru.shchepetkov.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomeRestController {

    private final MessageService messageService;
    private final UserService userService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getHomeStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long totalMessages = messageService.getMessageCount();
            long totalUsers = userService.findAll().size();
            long activeUsers = userService.findAll().stream()
                    .filter(user -> user.isActive())
                    .count();
            
            response.put("success", true);
            response.put("totalMessages", totalMessages);
            response.put("totalUsers", totalUsers);
            response.put("activeUsers", activeUsers);
            response.put("inactiveUsers", totalUsers - activeUsers);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get stats: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            
            if (username != null && !username.equals("anonymousUser")) {
                response.put("success", true);
                response.put("username", username);
                response.put("authenticated", true);
            } else {
                response.put("success", true);
                response.put("authenticated", false);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get current user: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/popular-tags")
    public ResponseEntity<Map<String, Object>> getPopularTags() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Long> popularTags = messageService.getPopularTags();
            
            response.put("success", true);
            response.put("popularTags", popularTags);
            response.put("totalTags", popularTags.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get popular tags: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<Map<String, Object>> getRecentActivity(
            @RequestParam(defaultValue = "5") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Message> recentMessages = messageService.findRecentMessages(limit);
            
            response.put("success", true);
            response.put("recentMessages", recentMessages);
            response.put("count", recentMessages.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get recent activity: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String type) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (query == null || query.isEmpty()) {
            response.put("success", false);
            response.put("message", "Search query is required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            if ("messages".equals(type) || type == null) {
                List<Message> messages = messageService.findByTextContaining(query);
                response.put("messages", messages);
                response.put("messageCount", messages.size());
            }
            
            if ("users".equals(type) || type == null) {
                List<User> users = userService.findAll().stream()
                        .filter(user -> user.getUsername() != null && 
                                      user.getUsername().toLowerCase().contains(query.toLowerCase()))
                        .collect(java.util.stream.Collectors.toList());
                response.put("users", users);
                response.put("userCount", users.size());
            }
            
            response.put("success", true);
            response.put("query", query);
            response.put("type", type);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Search failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 
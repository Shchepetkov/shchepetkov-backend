package ru.shchepetkov.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.shchepetkov.models.Message;
import ru.shchepetkov.models.User;
import ru.shchepetkov.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/view")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageViewController {

    private final MessageService messageService;

    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages(
            @RequestParam(required = false, defaultValue = "") String filter) {
        
        Map<String, Object> response = new HashMap<>();
        List<Message> messages;

        if (filter != null && !filter.isEmpty()) {
            messages = messageService.findByTag(filter);
        } else {
            messages = messageService.findAll();
        }

        response.put("messages", messages);
        response.put("filter", filter);
        response.put("count", messages.size());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/messages")
    public ResponseEntity<Map<String, Object>> addMessage(
            @AuthenticationPrincipal User user,
            @RequestBody MessageRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (user == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }

        try {
            Message message = messageService.createMessage(request.getText(), request.getTag(), user);
            
            response.put("success", true);
            response.put("message", "Message added successfully");
            response.put("messageId", message.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add message: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/messages/search")
    public ResponseEntity<Map<String, Object>> searchMessages(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Long authorId) {
        
        Map<String, Object> response = new HashMap<>();
        List<Message> messages;

        if (text != null && !text.isEmpty()) {
            // Поиск по тексту (нужно добавить в MessageService)
            messages = messageService.findAll(); // Пока возвращаем все
        } else if (tag != null && !tag.isEmpty()) {
            messages = messageService.findByTag(tag);
        } else if (authorId != null) {
            messages = messageService.findByAuthorId(authorId);
        } else {
            messages = messageService.findAll();
        }

        response.put("messages", messages);
        response.put("count", messages.size());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages/popular-tags")
    public ResponseEntity<Map<String, Object>> getPopularTags() {
        Map<String, Object> response = new HashMap<>();
        
        // Получаем все сообщения и подсчитываем популярные теги
        List<Message> allMessages = messageService.findAll();
        Map<String, Long> tagCounts = new HashMap<>();
        
        for (Message message : allMessages) {
            if (message.getTag() != null && !message.getTag().isEmpty()) {
                tagCounts.put(message.getTag(), tagCounts.getOrDefault(message.getTag(), 0L) + 1);
            }
        }
        
        response.put("popularTags", tagCounts);
        response.put("totalTags", tagCounts.size());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages/recent")
    public ResponseEntity<Map<String, Object>> getRecentMessages(
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        List<Message> allMessages = messageService.findAll();
        
        // Берем последние сообщения (в реальном приложении нужно добавить сортировку по дате)
        List<Message> recentMessages = allMessages.subList(
            Math.max(0, allMessages.size() - limit), 
            allMessages.size()
        );
        
        response.put("messages", recentMessages);
        response.put("count", recentMessages.size());
        
        return ResponseEntity.ok(response);
    }

    public static class MessageRequest {
        private String text;
        private String tag;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public String getTag() { return tag; }
        public void setTag(String tag) { this.tag = tag; }
    }
} 
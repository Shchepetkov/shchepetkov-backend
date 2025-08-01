package ru.shchepetkov.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shchepetkov.models.Message;
import ru.shchepetkov.models.User;
import ru.shchepetkov.service.MessageService;
import ru.shchepetkov.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageRestController {

    private final MessageService messageService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.findAll();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        Optional<Message> message = messageService.findById(id);
        return message.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody MessageRequest request) {
        try {
            Optional<User> author = userService.findById(request.getAuthorId());
            if (author.isPresent()) {
                Message message = messageService.createMessage(request.getText(), request.getTag(), author.get());
                return ResponseEntity.status(HttpStatus.CREATED).body(message);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Message> updateMessage(@PathVariable Long id, @RequestBody Message message) {
        Optional<Message> existingMessage = messageService.findById(id);
        if (existingMessage.isPresent()) {
            message.setId(id);
            Message updatedMessage = messageService.save(message);
            return ResponseEntity.ok(updatedMessage);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        Optional<Message> message = messageService.findById(id);
        if (message.isPresent()) {
            messageService.deleteMessage(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Message>> getMessagesByTag(@PathVariable String tag) {
        List<Message> messages = messageService.findByTag(tag);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Message>> getMessagesByAuthor(@PathVariable Long authorId) {
        List<Message> messages = messageService.findByAuthorId(authorId);
        return ResponseEntity.ok(messages);
    }

    // DTO для создания сообщения
    public static class MessageRequest {
        private String text;
        private String tag;
        private Long authorId;

        // Getters and Setters
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public String getTag() { return tag; }
        public void setTag(String tag) { this.tag = tag; }
        public Long getAuthorId() { return authorId; }
        public void setAuthorId(Long authorId) { this.authorId = authorId; }
    }
} 
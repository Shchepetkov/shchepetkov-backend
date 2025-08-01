package ru.shchepetkov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.shchepetkov.models.Message;
import ru.shchepetkov.models.User;
import ru.shchepetkov.repository.MessageRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }

    public List<Message> findByTag(String tag) {
        return messageRepository.findByTag(tag);
    }

    public List<Message> findByAuthorId(Long authorId) {
        return messageRepository.findByAuthorId(authorId);
    }

    public Message createMessage(String text, String tag, User author) {
        Message message = new Message(text, tag, author);
        return messageRepository.save(message);
    }

    public List<Message> findByTextContaining(String text) {
        // Простой поиск по тексту (в реальном приложении лучше использовать полнотекстовый поиск)
        List<Message> allMessages = messageRepository.findAll();
        return allMessages.stream()
                .filter(message -> message.getText() != null && 
                                 message.getText().toLowerCase().contains(text.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Message> findRecentMessages(int limit) {
        List<Message> allMessages = messageRepository.findAll();
        int startIndex = Math.max(0, allMessages.size() - limit);
        return allMessages.subList(startIndex, allMessages.size());
    }

    public Map<String, Long> getPopularTags() {
        List<Message> allMessages = messageRepository.findAll();
        Map<String, Long> tagCounts = new HashMap<>();
        
        for (Message message : allMessages) {
            if (message.getTag() != null && !message.getTag().isEmpty()) {
                tagCounts.put(message.getTag(), tagCounts.getOrDefault(message.getTag(), 0L) + 1);
            }
        }
        
        return tagCounts;
    }

    public long getMessageCount() {
        return messageRepository.count();
    }

    public long getMessageCountByAuthor(Long authorId) {
        return messageRepository.findByAuthorId(authorId).size();
    }

    public long getMessageCountByTag(String tag) {
        return messageRepository.findByTag(tag).size();
    }
} 
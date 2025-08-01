package ru.shchepetkov.dto;

public class MessageDto {
    private Long id;
    private String text;
    private String tag;
    private Long authorId;
    private String authorUsername;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
} 
package com.example.backend.dto;

public class ChatbotRequest {
    private String message;
    private String role;
    private String context;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
}
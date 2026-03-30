package com.example.backend.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private long expiresInSeconds;
    private Long linkedEntityId;

    public AuthResponse(String token, String username, String role, long expiresInSeconds, Long linkedEntityId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.expiresInSeconds = expiresInSeconds;
        this.linkedEntityId = linkedEntityId;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
    public Long getLinkedEntityId() { return linkedEntityId; }
}

package com.example.backend.dto;

public class ChatbotResponse {
    private String reply;

    public ChatbotResponse(String reply) {
        this.reply = reply;
    }

    public String getReply() { return reply; }
}
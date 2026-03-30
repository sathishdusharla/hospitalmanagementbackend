package com.example.backend.controller;

import com.example.backend.dto.ChatbotRequest;
import com.example.backend.dto.ChatbotResponse;
import com.example.backend.service.HuggingFaceChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private HuggingFaceChatService huggingFaceChatService;

    @PostMapping("/message")
    public ResponseEntity<?> message(@RequestBody ChatbotRequest request) {
        try {
            String reply = huggingFaceChatService.ask(request.getMessage(), request.getRole(), request.getContext());
            return ResponseEntity.ok(new ChatbotResponse(reply));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
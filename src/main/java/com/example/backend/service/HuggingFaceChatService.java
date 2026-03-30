package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HuggingFaceChatService {

    private final HttpClient httpClient;
    private static final Pattern GENERATED_TEXT_PATTERN = Pattern.compile("\\\"generated_text\\\"\\s*:\\s*\\\"(.*?)\\\"", Pattern.DOTALL);
    private static final Pattern ERROR_PATTERN = Pattern.compile("\\\"error\\\"\\s*:\\s*\\\"(.*?)\\\"", Pattern.DOTALL);

    @Value("${huggingface.api.key:}")
    private String apiKey;

    @Value("${huggingface.model:google/flan-t5-large}")
    private String model;

    public HuggingFaceChatService() {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
    }

    public String ask(String message) throws Exception {
        return ask(message, null, null);
    }

    public String ask(String message, String role, String context) throws Exception {
        if (message == null || message.isBlank()) {
            throw new Exception("Message cannot be empty");
        }

        String trimmed = message.length() > 2000 ? message.substring(0, 2000) : message;
        String rolePrefix = "";
        if (role != null && !role.isBlank()) {
            if ("DOCTOR".equalsIgnoreCase(role)) {
                rolePrefix = "You are MediTrust doctor assistant. Help with patient management, diagnosis, prescriptions, and lab test orders. ";
            } else if ("PATIENT".equalsIgnoreCase(role)) {
                rolePrefix = "You are MediTrust patient assistant. Help with appointment booking, viewing prescriptions, lab tests, and health information. ";
            } else if ("ADMIN".equalsIgnoreCase(role)) {
                rolePrefix = "You are MediTrust admin assistant. Help with hospital management, user administration, appointment oversight, and system controls. ";
            }
        }
        String contextStr = context != null && !context.isBlank() ? " Context: " + context : "";
        String prompt = rolePrefix + "You are the in-app assistant for MediTrust hospital management application. " +
            "Answer only about application usage, user flows, and features. " +
            "Available modules include: register/login, patient dashboard, appointment booking, doctor dashboard, diagnosis, treatment, prescription, lab tests, and admin dashboard. " +
            "If a question is not about the application, politely redirect to app-related help." + contextStr +
            " Question: " + trimmed + "\nAnswer:";
        String escaped = escapeJson(prompt);
        String requestBody = "{" +
            "\"inputs\":\"" + escaped + "\"," +
            "\"parameters\":{" +
            "\"max_new_tokens\":220," +
            "\"temperature\":0.3," +
            "\"return_full_text\":false" +
            "}" +
            "}";

        Set<String> candidateModelSet = new LinkedHashSet<>();
        candidateModelSet.add(model);
        candidateModelSet.add("google/flan-t5-base");
        candidateModelSet.add("google/flan-t5-small");
        List<String> candidateModels = new ArrayList<>(candidateModelSet);

        String lastError = null;
        for (String candidateModel : candidateModels) {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("https://api-inference.huggingface.co/models/" + candidateModel))
                .timeout(Duration.ofSeconds(45))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));

            if (apiKey != null && !apiKey.isBlank()) {
                requestBuilder.header("Authorization", "Bearer " + apiKey);
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body() == null ? "" : response.body();

            if (response.statusCode() >= 400) {
                if ((response.statusCode() == 401 || response.statusCode() == 403) && (apiKey == null || apiKey.isBlank())) {
                    lastError = "Hugging Face API key missing or unauthorized.";
                    continue;
                }

                // Model unavailable/deprecated/not found: try next candidate model.
                if (response.statusCode() == 404 || response.statusCode() == 410 || response.statusCode() == 503) {
                    lastError = "Model " + candidateModel + " unavailable (status " + response.statusCode() + ")";
                    continue;
                }

                lastError = "Hugging Face request failed with status " + response.statusCode();
                continue;
            }

            Matcher errorMatcher = ERROR_PATTERN.matcher(responseBody);
            if (errorMatcher.find()) {
                lastError = "Hugging Face error: " + unescapeJson(errorMatcher.group(1));
                continue;
            }

            Matcher generatedTextMatcher = GENERATED_TEXT_PATTERN.matcher(responseBody);
            if (generatedTextMatcher.find()) {
                String output = unescapeJson(generatedTextMatcher.group(1)).trim();
                if (!output.isBlank()) {
                    return output;
                }
            }

            lastError = "No generated_text returned by model " + candidateModel;
        }

        if (lastError != null) {
            return appOfflineFallback(trimmed, lastError);
        }

        return appOfflineFallback(trimmed, "No response from model");
    }

    private String appOfflineFallback(String question, String reason) {
        String q = question == null ? "" : question.toLowerCase();
        String guidance;

        if (q.contains("register") || q.contains("sign up") || q.contains("signup")) {
            guidance = "To register: open Register page, select role, fill mandatory details, submit, then login with the same username/password.";
        } else if (q.contains("login") || q.contains("invalid credential") || q.contains("password")) {
            guidance = "For login issues: ensure you use the same credentials used during registration, verify role, and try again. If needed, create a fresh user and test login immediately.";
        } else if (q.contains("book") || q.contains("appointment") || q.contains("patient dashboard")) {
            guidance = "To book an appointment: login as PATIENT, open Patient Dashboard, select patient profile, choose hospital and doctor, set date/time, add symptoms, review consultation fee/day patient number, then submit.";
        } else if (q.contains("doctor") || q.contains("treat") || q.contains("diagnosis")) {
            guidance = "Doctor flow: login as DOCTOR, open Doctor Dashboard, select an appointment, review patient details/history, click Treat, add diagnosis, then create prescription from eligible consultations.";
        } else if (q.contains("prescription") || q.contains("medicine")) {
            guidance = "Prescription can be created after treatment/diagnosis is recorded for a consultation. Open doctor prescriptions section, select consultation, add medicines/notes, and save.";
        } else if (q.contains("admin") || q.contains("dashboard")) {
            guidance = "Admin Dashboard provides high-level management and monitoring. Ensure ADMIN role access and check relevant sections for data and controls.";
        } else if (q.contains("chatbot") || q.contains("ai")) {
            guidance = "This assistant answers MediTrust app usage questions. Ask tasks like: how to register, how to book appointment, why doctor cannot prescribe, or where to see treated patients.";
        } else {
            guidance = "I can help with MediTrust application usage. Ask about register/login, appointment booking, doctor treatment flow, diagnosis, prescription, or admin actions.";
        }

        return "AI service is temporarily unavailable. " + guidance;
    }

    private String escapeJson(String value) {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    private String unescapeJson(String value) {
        return value
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\");
    }
}
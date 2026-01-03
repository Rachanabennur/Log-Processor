package com.rachana.processor.log.generator;

import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ApiLogService {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String fetchLiveLogMessage() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.adviceslip.com/advice"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Using Jackson to parse: {"slip": {"id": 1, "advice": "..."}}
            JsonNode root = objectMapper.readTree(response.body());
            return root.path("slip").path("advice").asString();

        } catch (Exception e) {
            return "EXTERNAL_API_ERROR: Connection timed out - " + e.getMessage();
        }
    }
}
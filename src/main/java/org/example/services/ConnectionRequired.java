package org.example.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Абстрактный класс взаимодействия по http
 */
public abstract class ConnectionRequired {
    private final HttpClient client = HttpClient
            .newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    private final ObjectMapper mapper = new ObjectMapper();
    private String endpoint;

    /**
     * Конструктор с заданием точки подключения
     *
     * @param endpoint - строка с адресом подключения
     */
    public ConnectionRequired(String endpoint){
        this.endpoint = endpoint;
    }

    /**
     * Экранирование текста на случай наличия кавычек или спец. символов
     *
     * @param text текст с потенциальными спец. симполами
     * @return текст с экранированными спец. символами
     */
    String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    CompletableFuture<String> sendPayload(String json){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }

    /**
     * Достает поле из текста тела json
     *
     * @param body тело json
     * @param name имя поля
     * @return значение поля в формате JsonNode
     */
    JsonNode extractField(String body, String name){
        try {
            JsonNode root = mapper.readTree(body);
            if (root.has(name)) {
                return root.get(name);
            } else {
                throw new IllegalStateException(
                        String.format("No \"%s\" field in response: %s", name, body)
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response from AI checker", e);
        }
    }
}

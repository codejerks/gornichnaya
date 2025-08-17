
package org.example.services;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.CompletableFuture;

/**
 * Взаимодействие с микросервисом проверки на мат
 */
public class CurseCheckingService extends ConnectionRequired implements MessageChecking {

    /**
     * Конструктор сервиса
     */
    public CurseCheckingService(String property) {
        super(System.getenv().getOrDefault("MICROSERVICE_URL",
                property));
    }

    /**
     * @param msg проверяемое сообщение
     * @return является ли сообщение допустимым
     */
    @Override
    public CompletableFuture<Boolean> check(Message msg) {

        String parsedText = this.escapeJson(this.extractText(msg));
        String json = "{\"text\": \""+parsedText+"\"}";

        return this.sendPayload(json)
                .thenApply(body -> this.extractField(body, "is_cursed").asInt())
                .thenApply(result -> result != 1);
    }
}

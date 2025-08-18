package org.example.services;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.CompletableFuture;

/**
 * Взаимодействие с микросервисом проверки на языковую модель
 */
public class AiCheckingService extends ConnectionRequired implements MessageChecking {

    @Override
    public String getName() {
        return "Фильтр нейросетевого спама";
    }

    @Override
    public String getIdentifier() {
        return "ai-vs-human";
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
                .thenApply(body -> this.extractField(body, "is_human").asInt())
                .thenApply(result -> result == 1);
    }
}

package org.example.services;

import java.util.concurrent.CompletableFuture;

import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Взаимодействие с микросервисом легкой проверки на спам с помощью ML
 */
public class EasyMLSpamCheckingService extends ConnectionRequired implements MessageChecking {

    @Override
    public String getName() {
        return "ML-фильтр спама";
    }

    @Override
    public String getIdentifier() {
        return "easy-ml-checking";
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
                .thenApply(body -> this.extractField(body, "result").asInt())
                .thenApply(result -> result == 0);
    }
}

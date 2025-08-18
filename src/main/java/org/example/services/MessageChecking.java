package org.example.services;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.CompletableFuture;

/**
 * Интерфейс для классов, проверяющих сообщение на корректность
 */
public interface MessageChecking {

    /**
     * Возвращает название checker-а
     * @return String имя сервиса
     */
    public String getName();

    /**
     * Возвращает идентификатор checker-а
     * @return String идентификатор
     */
    public String getIdentifier();

    /**
     * Функция проверки сообщения на корректность
     *
     * @param msg сообщение
     * @return CompletableFuture true/false в зависимости от исхода проверки
     */
    public CompletableFuture<Boolean> check(Message msg);

    /**
     * Функция для получения текста сообщения
     *
     * @param msg полученное сообщение
     * @return String text текст или подпись из сообщения или null в случае отсутствия обоих
     */
    public default String extractText(Message msg) {
        String text = msg.hasText() ? msg.getText() : msg.getCaption();
        if (text.isBlank()) return null;
        return text;
    }
}
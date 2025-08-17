package org.example.services;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * Gateway взаимодействия с микросервисами
 */
public class ServiceManager {
    Properties prop = loadProperties();
    private final CurseCheckingService curseCheckingService = new CurseCheckingService(prop.getProperty("curse.url"));
    private final EasyMLSpamCheckingService easyMLSpamCheckingService = new EasyMLSpamCheckingService(prop.getProperty("mlspam.url"));
    private final DBService dbService = new DBService();
    private final AiCheckingService aiCheckingService = new AiCheckingService(prop.getProperty("ai.url"));
    private final RedisService redisService = new RedisService(prop.getProperty("redis.host"), prop.getProperty("redis.port"));
    private final EroticScamCheckingService eroticScamCheckingService = new EroticScamCheckingService();

    /**
     * Конструктор по умолчанию
     */
    public ServiceManager(){}

    /**
     * Обработчик сообщений с помощью микросервисов
     *
     * @param msg проверяемое сообщение
     * @return результат проверки
     */
    public CompletableFuture<Boolean> processMessage(Message msg){
        CompletableFuture<Boolean> result = CompletableFuture.completedFuture(true);

        // Массив необходимых проверок; после добавления кастомизации проверки
        // чата можно будет получать его отдельным методом по информации из БД
        ArrayList<MessageChecking> checkers = new ArrayList<>();
        checkers.add(eroticScamCheckingService);
        checkers.add(curseCheckingService);
        checkers.add(easyMLSpamCheckingService);
        checkers.add(aiCheckingService);

        // Цикл проверки каждым checker-ом
        for (MessageChecking checker : checkers) {
            result = result.thenCompose(currentResult -> {
                // Прерываем цепочку, если предыдущая проверка провалилась
                if (!currentResult) {
                    return CompletableFuture.completedFuture(false);
                }
                // Выполняем проверку текущим checker-ом
                return checker.check(msg)
                        .thenApply(valid -> {
                            if (!valid) System.out.println("  \u001B[33m" +
                                    "Spam detected by " + checker.getClass().getSimpleName() +
                                    "\u001B[0m");
                            return valid;
                        })
                        .exceptionally(ex -> {
                            System.err.println("  Check failed in " + checker.getClass().getSimpleName() +
                                    ": " + ex.getMessage());
                            return true;
                        });
            });
        }
        return result;
    }
    private static Properties loadProperties() {
        try {
            Properties prop = new Properties();
            try (InputStream input = ServiceManager.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new FileNotFoundException("config.properties not found, check resources");
                }
                prop.load(input);
                return prop;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties", e);
        }
    }
}


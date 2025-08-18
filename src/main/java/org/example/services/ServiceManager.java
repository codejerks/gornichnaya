package org.example.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;

import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Gateway взаимодействия с микросервисами
 */
public class ServiceManager {
    private final HashMap<String, MessageChecking> allowedServices = new HashMap<>();

    /// Позже убрать
    private final FakeDBService dbService = new FakeDBService();

    /**
     * Конструктор по умолчанию
     */
    public ServiceManager(){
        registerServices();
    };

    public HashMap<Long, String> getChatsFromDB(long userId){
        return dbService.getChatsByUserId(userId);
    }

    public void addUserChatToDB(long chatId, String chatName, long userId){
        dbService.addChatForUser(chatId, chatName, userId);
    }

    public HashMap<MessageChecking, Boolean> getChatSettingsFromDB(long chatId){
        HashMap<String, Boolean> identifierToBoolMap = dbService.getChatSettings(chatId);

        HashMap<MessageChecking, Boolean> chatSettings = new HashMap<>();
        identifierToBoolMap.forEach((k, v) -> {
            MessageChecking checker = allowedServices.get(k);
            if (checker != null) chatSettings.put(checker, v);
        });

        return chatSettings;
    }

    public HashMap<MessageChecking, Boolean> changeChatSettingInDB(long chatId, String settingToChange){
        dbService.changeChatSetting(chatId, settingToChange);
        HashMap<String, Boolean> identifierToBoolMap = dbService.getChatSettings(chatId);

        HashMap<MessageChecking, Boolean> chatSettings = new HashMap<>();
        identifierToBoolMap.forEach((k, v) -> {
            MessageChecking checker = allowedServices.get(k);
            if (checker != null) chatSettings.put(checker, v);
        });

        return chatSettings;
    }

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
        Collection<MessageChecking> checkers = allowedServices.values();

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

    /**
     * Создает объекты сервисов
     * <p>
     *     Инициализирует объекты из классов, перечисленных
     *     в META-INF/services/org.example.services.MessageChecking
     * </p>
     */
    private void registerServices(){
        ServiceLoader<MessageChecking> loader = ServiceLoader.load(MessageChecking.class);
        Properties prop = loadProperties();

        for (MessageChecking service : loader) {
            allowedServices.put(service.getIdentifier(), service);
            System.out.println("    \u001B[36m"+"Service "+service.getIdentifier()+" was loaded"+"\u001B[0m");

            if (service instanceof ConnectionRequired connectionRequired){
                String endpoint = prop.getProperty(service.getIdentifier()+".url");
                connectionRequired.setEndpoint(endpoint);
                System.out.println("    \u001B[36m"+"Set endpoint for "+service.getIdentifier()+": \""+endpoint+"\"");
            }
        }
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

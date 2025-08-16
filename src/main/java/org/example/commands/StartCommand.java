package org.example.commands;

import org.example.services.ServiceManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик команды <b>/start</b>
 */
public class StartCommand extends BotCommand {

    /**
     * Конструктор по умолчанию
     */
    public StartCommand(){};

    @Override
    public String getCommandIdentifier() {
        return "/start";
    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot, ServiceManager serviceManager) {
        if (!isPrivateChat(msg)) return;

        String userName = getUserName(msg);
        String message ="""
                Привет!
                Я AI-горничная для твоего тг канала 🧹

                В мой функционал входит:

                🧹 Обнаружение и очистка спама с помощью современных ml-решений

                👾 Детекция нейросетевого спама с помощью LLM от Google

                🛡 Надежная система кеширования для защиты от спам-атак

                Чтобы воспользоваться моими функциями, нужно:
                 - Добавить меня в телеграм-чат
                 - Выдать права администратора
                
                Мы находимся в демонстрационной версии бота, так что в чате ниже ты можешь проверить мой функционал: просто отправь немного спама, и я ловко его почищу 😉
                """;
        sendMessage(msg, bot, message);
    }

    /**
     * Проверяет, что обновление пришло из приватного чата
     */
    private boolean isPrivateChat(Message msg) {
        return "private".equals(msg.getChat().getType());
    }
}
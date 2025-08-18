package org.example.commands;

import org.example.services.ServiceManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик команды <b>/start</b>
 */
public class StartCommand extends BotCommand {
    private String messageText ="""
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
    private List<InlineKeyboardButton> keyboardRow = List.of(
            InlineKeyboardButton
                    .builder()
                    .text("Поехали!")
                    .callbackData("/menu")
                    .build()
    );

    /**
     * Конструктор по умолчанию
     */
    public StartCommand(){};

    @Override
    public String getCommandIdentifier() {
        return "/start";
    }

    @Override
    public void handleCommand(Message msg, TelegramLongPollingBot bot, ServiceManager serviceManager) {
        if (!isPrivateChat(msg)) return;

        Long chatId = msg.getChatId();
        Long userId = msg.getFrom().getId();
        String userName = msg.getFrom().getUserName();
        serviceManager.addUserToDB(userId, userName);
        serviceManager.addUserChatToDB(chatId, "Этот чат", userId);
        sendMessage(chatId, messageText, List.of(keyboardRow), bot);

    }
}
package org.example.commands;

import org.example.services.ServiceManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.print.attribute.standard.Severity;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.toIntExact;

public class SettingsCommand extends BotCommand{
    private String messageText ="""
                Выбери чат, который нужно бы настроить
                """;
    private InlineKeyboardButton backButton = InlineKeyboardButton
            .builder()
            .text("Назад")
            .callbackData("/menu")
            .build();

    @Override
    public String getCommandIdentifier() {
        return "/settings";
    }

    @Override
    public boolean isCallbackConfiguration(String configuration){
        return getCommandIdentifier().equals(configuration);
    };

    @Override
    public void handleCommand(Message msg, TelegramLongPollingBot bot, ServiceManager serviceManager){
        if (! isPrivateChat(msg)) return;

        Long chatId = msg.getChatId();
        long userId = msg.getFrom().getId();
        HashMap<Long, String> chats= serviceManager.getChatsFromDB(userId);

        sendMessage(chatId, messageText, buildKeyboard(chats), bot);
    };

    @Override
    public void handleCallback(CallbackQuery callbackQuery, TelegramLongPollingBot bot, ServiceManager serviceManager){

        long messageId = callbackQuery.getMessage().getMessageId();
        long chatId = callbackQuery.getMessage().getChatId();
        long userId = callbackQuery.getFrom().getId();
        HashMap<Long, String> chats= serviceManager.getChatsFromDB(userId);

        editMessage(chatId, messageId, messageText, buildKeyboard(chats), bot);
    }

    private List<List<InlineKeyboardButton>> buildKeyboard(HashMap<Long, String> chats){
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        chats.forEach((chatId, chatName) ->
                keyboard.add(List.of(
                        InlineKeyboardButton
                            .builder()
                            .text(chatName)
                            .callbackData("/chatSettings_"+chatId.toString())
                            .build()
                ))
        );
        keyboard.add(List.of(backButton));
        return keyboard;
    }
}

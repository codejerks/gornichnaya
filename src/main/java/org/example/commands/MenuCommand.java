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

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

public class MenuCommand extends BotCommand{

    private String messageText ="""
                <b>Главное меню Горничной</b>
                
                Чтобы начать пользоваться ботом, выбери <b>Настроить чаты</b>
                """;
    private List<InlineKeyboardButton> keyboardRow = List.of(
        InlineKeyboardButton
                .builder()
                .text("Подробнее о боте")
                .callbackData("/help")
                .build(),
        InlineKeyboardButton
                .builder()
                .text("Настроить чаты")
                .callbackData("/settings")
                .build()
    );

    @Override
    public String getCommandIdentifier() {
        return "/menu";
    }

    @Override
    public boolean isCallbackConfiguration(String configuration){
        return getCommandIdentifier().equals(configuration);
    };

    @Override
    public void handleCommand(Message msg, TelegramLongPollingBot bot, ServiceManager serviceManager) {
        if (!isPrivateChat(msg)) return;
        Long chatId = msg.getChatId();
        sendMessage(chatId, messageText, List.of(keyboardRow), bot);
    }

    @Override
    public void handleCallback(CallbackQuery callbackQuery, TelegramLongPollingBot bot, ServiceManager serviceManager){
        long messageId = callbackQuery.getMessage().getMessageId();
        long chatId = callbackQuery.getMessage().getChatId();
        editMessage(chatId, messageId, messageText, List.of(keyboardRow), bot);
    }
}

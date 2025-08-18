package org.example.commands;

import org.example.services.MessageChecking;
import org.example.services.ServiceManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatSettingsCommand extends BotCommand{
    private InlineKeyboardButton backButton = InlineKeyboardButton
                    .builder()
                    .text("Вернуться в меню")
                    .callbackData("/menu")
                    .build();

    @Override
    public String getCommandIdentifier() {
        return "/chatSettings";
    }

    @Override
    public boolean isCallbackConfiguration(String configuration){
        return  (configuration != null &&
                configuration.matches("^"+getCommandIdentifier()+"_(-?\\d+)(_\\S+)?$"));
    }

    @Override
    public void handleCallback(CallbackQuery callbackQuery, TelegramLongPollingBot bot, ServiceManager serviceManager){
        String callData = callbackQuery.getData();

        String[] args = callData.substring(14).split("_");
        if (args.length > 2) return;
        long editedChatId = Long.parseLong(args[0]);

        long messageId = callbackQuery.getMessage().getMessageId();
        long chatId = callbackQuery.getMessage().getChatId();

        HashMap<MessageChecking, Boolean> chatSettings;

        if (args.length == 2) chatSettings = serviceManager.changeChatSettingInDB(editedChatId, args[1]);
        else chatSettings = serviceManager.getChatSettingsFromDB(editedChatId);

        editMessage(
                chatId,
                messageId,
                "Отредактируйте пайплайн проверки",
                buildKeyboard(chatSettings, editedChatId),
                bot);
    }

    public List<List<InlineKeyboardButton>> buildKeyboard(HashMap<MessageChecking, Boolean> chatSettings, long editedChatId) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        chatSettings.forEach((k, v)->
                keyboard.add(List.of(
                        InlineKeyboardButton
                                .builder()
                                .text((v?"✅ ":"❌ ") + k.getName())
                                .callbackData("/chatSettings_"+editedChatId+"_"+k.getIdentifier())
                                .build()
                )));
        keyboard.add(List.of(backButton));
        return keyboard;
    }
}

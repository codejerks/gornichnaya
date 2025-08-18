package org.example.commands;

import org.example.services.ServiceManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Math.toIntExact;

/**
 * Абстрактный класс обработчика команд бота
 */
public abstract class BotCommand {

    /**
     * Конструктор по умолчанию
     */
    public BotCommand(){};

    /**
     * Геттер идентификатора команды вида <b>/some_command</b>
     * @return String идентификатор команды
     */
    public String getCommandIdentifier(){ return null; };

    /**
     * Проверка удовлетворительной конфигурации callback
     *
     * <p>Возвращает false, если не переопределен (подразумевая, что команда не обрабатывает callback)</p>
     * @return true|false результат проверски
     */
    public boolean isCallbackConfiguration(String configuration){
        return false;
    };

    /**
     * Обработчик команды
     *
     * @param msg объект класса Message, содержащий информацию об отправленном сообщении
     * @param bot объект класса бота, нужен для отправки текста в ответ на вызов команды, если требуется
     * @param serviceManager объект класса serviceManager, нужен в случае необходимости связи с сервисами
     *                       (например, обновление настроек в базе данных)
     */
    public void handleCommand(Message msg, TelegramLongPollingBot bot, ServiceManager serviceManager){};

    /**
     * Обработчик callback
     *
     * @param callbackQuery - объект CallbackQuery, содержащий информацию о вызванном callback
     * @param bot объект класса бота, нужен для отправки текста в ответ на вызов команды, если требуется
     * @param serviceManager объект класса serviceManager, нужен в случае необходимости связи с сервисами
     *                       (например, обновление настроек в базе данных)
     */
    public void handleCallback(CallbackQuery callbackQuery, TelegramLongPollingBot bot, ServiceManager serviceManager){}

    protected boolean sendMessage(long chatId,
                                  String messageText,
                                  List<List<InlineKeyboardButton>> keyboard,
                                  TelegramLongPollingBot bot){
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(messageText)
                .parseMode("Markdown")
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(keyboard)
                        .build()
                ).build();
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        };
        return true;
    }

    protected boolean editMessage(long chatId,
                                  long messageId,
                                  String messageText,
                                  List<List<InlineKeyboardButton>> keyboard,
                                  TelegramLongPollingBot bot){
        EditMessageText new_message = EditMessageText.builder()
                .chatId(chatId)
                .messageId(toIntExact(messageId))
                .text(messageText)
                .parseMode("HTML")
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(keyboard)
                        .build()
                ).build();
        try {
            bot.execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        };
        return true;
    }

    /**
     * Проверяет, что обновление пришло из приватного чата
     *
     * @param msg Объект сообщения
     */
    protected boolean isPrivateChat(Message msg) {
        return "private".equals(msg.getChat().getType());
    }
}
package org.example.commands;

import org.example.services.ServiceManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
    public abstract String getCommandIdentifier();

    /**
     * Обработчик команды
     *
     * @param msg объект класса Message, содержащий информацию об отправленном сообщении
     * @param bot объект класса бота, нужен для отправки текста в ответ на вызов команды, если требуется
     * @param serviceManager объект класса serviceManager, нужен в случае необходимости связи с сервисами
     *                       (например, обновление настроек в базе данных)
     */
    public abstract void handle(Message msg, TelegramLongPollingBot bot, ServiceManager serviceManager);

    /**
     * Отправляет сообщение в чат с ботом
     * @param msg сообщение, вызвавшее команду
     * @param bot объект класса бота
     * @param text текстовое сообщение
     */
    protected void sendMessage(Message msg, TelegramLongPollingBot bot, String text) {
        Long chatId = getChatId(msg);
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    /**
     * Возвращает имя пользователя сообщения внутри обновления
     * @param msg объект класса Message, содержащий информацию об отправленном сообщении
     * @return String имя пользователя
     */
    protected String getUserName(Message msg) {
        return msg.getFrom().getUserName();
    }

    /**
     * Возвразает идентификатор чата, куда было отправлено сообщение
     * @param msg объект класса Message, содержащий информацию об отправленном сообщении
     * @return Long идентификатор чата
     */
    protected Long getChatId(Message msg) {
        return msg.getChatId();
    }
}
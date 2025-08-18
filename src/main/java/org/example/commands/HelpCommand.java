package org.example.commands;

import org.example.services.ServiceManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.HashMap;
import java.util.List;

public class HelpCommand extends BotCommand{
    private List<InlineKeyboardButton> keyboardRow = List.of(
            InlineKeyboardButton
                    .builder()
                    .text("Тут будет ссылка")
                    .callbackData("/menu")
                    .build(),
            InlineKeyboardButton
                    .builder()
                    .text("Назад")
                    .callbackData("/menu")
                    .build()
    );

    private String messageText ="""
            **Горничная** — это антиспам телеграм-бот для автоматической модерации чатов и каналов. Он(-а, зависит от того, бот это или горничная) защищает ваше сообщество от спама, флуда, нежелательного контента и ботов, используя многоуровневую систему фильтрации.
                            
            ✅**Фильтрация флуда** — ограничивает отправку одинаковых сообщений с возможностью блокировки(мута) пользователей, которые отправляют идентичный текст много раз подряд. \s
            ✅ **Обнаружение ИИ-генерации** — распознаёт сообщения, созданные ChatGPT и другими нейросетями (если это запрещено правилами чата). \s
            ✅ **Защита от матов и оскорблений** — автоматически удаляет нецензурную лексику и токсичные высказывания. \s
            ✅ **Блокировка эротики и NSFW-контента** — скрывает или удаляет тексты 18+.
                            
            ####  **Как это работает?
                            
            1. Бот анализирует каждое сообщение в реальном времени.
            2. Если контент нарушает правила, бот удаляет его или ограничивает пользователя (мут/бан).
            3. Админы получают уведомления о действиях бота (можно настроить).
                            
            #### 🔹 **Почему стоит использовать?**
                            
            ✔️ **Экономит время** — не нужно вручную удалять спам. \s
            ✔️ **Уменьшает нагрузку на модераторов** — бот справляется с 90%нарушений(или иная статистическая информация). \s
            ✔️ **Подходит для любых чатов** — от небольших групп до крупных каналов.
                            
            Вопросы, жалобы и предложения просим вас оставлять по ссылке ниже. Обратная связь очень поможет нам стать еще удобнее и полезнее для вас.
                """;

    @Override
    public String getCommandIdentifier() {
        return "/help";
    }

    @Override
    public boolean isCallbackConfiguration(String configuration){
        return getCommandIdentifier().equals(configuration);
    };

    @Override
    public void handleCommand(Message msg, TelegramLongPollingBot bot, ServiceManager serviceManager){
        if (! isPrivateChat(msg)) return;
        Long chatId = msg.getChatId();
        sendMessage(chatId, messageText, List.of(keyboardRow), bot);
    };

    @Override
    public void handleCallback(CallbackQuery callbackQuery, TelegramLongPollingBot bot, ServiceManager serviceManager){
        long messageId = callbackQuery.getMessage().getMessageId();
        long chatId = callbackQuery.getMessage().getChatId();
        editMessage(chatId, messageId, messageText, List.of(keyboardRow), bot);
    }
}

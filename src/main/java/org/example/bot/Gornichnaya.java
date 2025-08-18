package org.example.bot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.ServiceLoader;

import org.example.commands.BotCommand;
import org.example.services.ServiceManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static java.lang.Math.toIntExact;

/**
 * Gornichnaya - класс телеграм-бота, реализующий взаимосвязь с пользователем
 */

public class Gornichnaya extends TelegramLongPollingBot {
    private final String BOT_USERNAME =
            System.getenv().getOrDefault("BOT_USERNAME", "gornichnaya_antispam_bot");
    private final ServiceManager serviceManager = new ServiceManager();
    private final HashMap<String, BotCommand> commands = new HashMap<>();

    /**
     * Конструктор класса, инициирующий экземпляр бота
     *
     * @throws IOException если токен бота не найден
     */
    public Gornichnaya() throws IOException {
        super(loadToken());
        registerCommands();
    }

    /**
     * Обработчик изменений состояний чатов
     *
     * @param update объект изменений в чатах
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message msg = update.getMessage();

            if (msg.getNewChatMembers().stream().anyMatch((user)->user.getUserName().equals(this.getBotUsername()))){
                System.out.println("\u001B[32m"+
                        msg.getFrom().getUserName()+
                        " added bot to "+msg.getChat().getTitle()+
                        "\u001B[0m");
                serviceManager.addUserChatToDB(msg.getChatId(), msg.getChat().getTitle(), msg.getFrom().getId());
            }


            if (!msg.hasText()) {
                return;
            }
            if (msg.isCommand()) {
                String commandIdentifier = msg.getText().split("\\s+")[0];
                BotCommand command = commands.get(commandIdentifier);
                if (command != null) command.handleCommand(msg, this, serviceManager);
                return;
            }
            serviceManager.processMessage(msg).thenAccept(result -> {
                if (!result) {
                    try {
                        execute(new DeleteMessage(msg.getChatId().toString(), msg.getMessageId()));
                        System.out.println("\u001B[36m" + "The message has been deleted" + "\u001B[0m");
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callData = callbackQuery.getData();

            String commandIdentifier = callData
                    .split("\\s+")[0]
                    .split("_")[0];
            BotCommand command = commands.get(commandIdentifier);

            if (command == null) return;
            if (! command.isCallbackConfiguration(callData)) return;

            command.handleCallback(callbackQuery, this, serviceManager);
        }
    }

    /**
     * @return Имя бота
     */
    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    /**
     * Загружает токен бота из файла <b>config.properties</b> (по ключу <b>bot.token</b>)
     *
     * @return Строка токена бота
     * @throws FileNotFoundException если не найден файл config.properties
     * @throws IOException если не получилось считать файл config.properties
     */
    private static String loadToken() {
        try {
            Properties prop = new Properties();
            try (InputStream input = Gornichnaya.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new FileNotFoundException("config.properties not found, check resources");
                }
                prop.load(input);
                return prop.getProperty("bot.token");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading token", e);
        }
    }

    /**
     * Создает объекты обработчиков команд
     * <p>
     *     Инициализирует объекты из классов, перечисленных
     *     в META-INF/services/org.example.commands.BotCommand
     * </p>
     */
    private void registerCommands(){
        ServiceLoader<BotCommand> loader = ServiceLoader.load(BotCommand.class);
        for (BotCommand command : loader) {
            commands.put(command.getCommandIdentifier(), command);
            System.out.println("    \u001B[36m"+"Command "+command.getCommandIdentifier()+" was loaded"+"\u001B[0m");
        }
    }
}
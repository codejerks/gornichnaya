package org.example;

import java.io.IOException;

import org.example.bot.Gornichnaya;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Точка входа в приложение
 */
public class App {
    /**
     * Конструктор по умолчанию
     */
    public App(){};

    /**
     * main метод запуска приложения
     * @param args параметры запуска
     */
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Gornichnaya());
            System.out.println("\u001B[36m"+"Bot started successfully"+"\u001B[0m");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
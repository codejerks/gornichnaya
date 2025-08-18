package it;

// src/test/java/org/example/it/ChannelFlowIT.java


import org.example.dao.ChatDao;
import org.example.dao.ChatSettingsDao;
import org.example.dao.UserDao;
import org.example.domain.Chat;
import org.example.domain.Users;
import org.example.domain.ChatSettings;
import org.example.services.DBService;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChannelFlowIT {

    static DBService db;
    static UserDao userDao;
    static ChatDao chatDao;
    static ChatSettingsDao settingsDao;

    @BeforeAll
    static void setup() throws Exception {
        // подключаемся к ОТДЕЛЬНОЙ тестовой БД
        DBService db = new DBService();

        userDao = new UserDao(db);
        chatDao = new ChatDao(db);
        settingsDao = new ChatSettingsDao(db);
    }

    @Test
    @Order(10)
    void createUser_twoChats_readNameAndSettings() throws Exception {
        // 1) создаём нового пользователя
        long userId = 1101L;
        userDao.insert(new Users(userId, "bob"));

        // 2) добавляем первый канал
        long chat1 = 2101L;
        chatDao.insert(new Chat(chat1, userId, "Bob First Channel"));

        // 3) добавляем второй канал
        long chat2 = 2102L;
        chatDao.insert(new Chat(chat2, userId, "Bob Second Channel"));

        // для второго канала создаём строку настроек с дефолтами
        int ins = settingsDao.insertDefaultsIfMissing(chat2);
        assertEquals(1, ins); // должна вставиться новая строка

        // 4) выводим (и проверяем) имя первого канала по его id
        String name1 = chatDao.findNameById(chat1).orElseThrow();
        System.out.println("chat1 name = " + name1);
        assertEquals("Bob First Channel", name1);

        // 5) выводим (и проверяем) настройки второго канала по его id
        ChatSettings s2 = settingsDao.findByChatId(chat2).orElseThrow();
        System.out.println(
                "chat2 settings: easyMl=" + s2.isEasyMlSpamCheck() +
                        ", ai=" + s2.isAiCheck() +
                        ", erotic=" + s2.isEroticCheck() +
                        ", curse=" + s2.isCurseCheck()
        );

        // дефолтные значения должны быть false
        assertFalse(s2.isEasyMlSpamCheck());
        assertFalse(s2.isAiCheck());
        assertFalse(s2.isEroticCheck());
        assertFalse(s2.isCurseCheck());
    }
}
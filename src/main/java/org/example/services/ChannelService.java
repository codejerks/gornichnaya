package org.example.services;



import org.example.dao.ChatDao;
import org.example.dao.UserDao;
import org.example.domain.Chat;
import org.example.domain.ChatSettings;
import org.example.domain.Users;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class ChannelService {
    private final DBService db;
    private final UserDao userDao;
    private final ChatDao channelDao;

    public ChannelService(DBService db) {
        this.db = db;
        this.userDao = new UserDao(db);
        this.channelDao = new ChatDao(db);
    }

    // 1) Добавить пользователя
    public void addUser(long userId, String username) throws SQLException {
        userDao.insert(new Users(userId, username));
    }

    public HashMap<Long,String> getChatsByUserId(long userId) throws SQLException {
        return channelDao.findIdNameByUserId(userId);
    }

    public boolean removeChat(long chatId) throws SQLException {
        return channelDao.deleteById(chatId) == 1;
    }

    // 2) Добавить канал пользователю (id и name задаёт приложение)
    public void addChatForUser(long channelId, String channelName, long userId) throws SQLException {
        channelDao.insert(new Chat(channelId, userId, channelName));
    }

    // 3) Получить только id каналов пользователя
    public List<Long> getChannelIdsByUser(long userId) throws SQLException {
        return channelDao.findIdsByUserId(userId);
    }

    public HashMap<String,Boolean> getChatSettings(long chatId) throws SQLException{
        return channelDao.findSettingsMapByChatId(chatId);
    }
    public int changeChatSetting(long chatId, String settingToChange)throws SQLException{
        return channelDao.updateSingleFlag(chatId, settingToChange);
    }
    // 4) Получить каналы пользователя с именами
    public List<Chat> getChannelsByUser(long userId) throws SQLException {
        return channelDao.findByUserId(userId);
    }

    // 5) Пример составной операции в одной транзакции (если понадобится)
    //    например: создать пользователя и первый канал атомарно
    public void createUserWithChannel(Users user, Chat chat) throws SQLException {
        try (Connection c = db.getConnection()) {
            try {
                c.setAutoCommit(false);

                // явные SQL через DAO, но с тем же Connection (простая версия ниже)
                // Здесь для краткости вызываем DAO как есть — у них свой Connection.
                // Если хотите одну транзакцию точно, перегрузите DAO-методы с Connection параметром.

                // Адаптер лямбдой: дать DAO один и тот же Connection
                ConnectionProvider sameConn = () -> c;

                new UserDao(sameConn).insert(user);
                new ChatDao(sameConn).insert(chat);

                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }
}

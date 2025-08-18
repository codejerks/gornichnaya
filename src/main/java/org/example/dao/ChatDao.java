package org.example.dao;

import org.example.domain.Chat;
import org.example.services.ConnectionProvider;

import java.sql.*;
import java.util.*;

public class ChatDao {
    private final ConnectionProvider cp;
    public ChatDao(ConnectionProvider cp) { this.cp = cp; }

    // Добавить чат пользователю
    public void insert(Chat chat) throws SQLException {
        final String sqlChat =
                "INSERT INTO chat (id, user_id, name) VALUES (?, ?, ?) " +
                        "ON CONFLICT (id) DO UPDATE SET " +
                        "user_id = EXCLUDED.user_id, " +
                        "name    = EXCLUDED.name";

        final String sqlSettings =
                "INSERT INTO chat_settings (chat_id, ai_check, curse_check, easy_ml_spam_check, erotic_check) " +
                        "VALUES (?, false, false, false, false) " +
                        "ON CONFLICT (chat_id) DO UPDATE SET " +
                        "ai_check          = EXCLUDED.ai_check, " +
                        "curse_check       = EXCLUDED.curse_check, " +
                        "easy_ml_spam_check= EXCLUDED.easy_ml_spam_check, " +
                        "erotic_check      = EXCLUDED.erotic_check";


        try (Connection c = cp.getConnection()) {
            // чтобы обе вставки были атомарными
            c.setAutoCommit(false);
            try (
                    PreparedStatement psChat = c.prepareStatement(sqlChat);
                    PreparedStatement psSettings = c.prepareStatement(sqlSettings)
            ) {
                // вставка в chat
                psChat.setLong(1, chat.getId());
                psChat.setLong(2, chat.getUserId());
                psChat.setString(3, chat.getName());
                psChat.executeUpdate();

                // вставка в chat_settings с дефолтными значениями
                psSettings.setLong(1, chat.getId());
                psSettings.executeUpdate();

                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }


    public HashMap<String, Boolean> findSettingsMapByChatId(long chatId) throws SQLException {
        final String sql = """
        SELECT easy_ml_spam_check, ai_check, erotic_check, curse_check
        FROM chat_settings
        WHERE chat_id = ?
        """;
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, chatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return new HashMap<>();
                HashMap<String, Boolean> m = new HashMap<>();
                m.put("ai-vs-human",     rs.getBoolean("ai_check"));
                m.put("curse-checking",  rs.getBoolean("curse_check"));
                m.put("easy-ml-checking",rs.getBoolean("easy_ml_spam_check"));
                m.put("erotic-scam",     rs.getBoolean("erotic_check"));
                return m;
            }
        }
    }

    public int updateSingleFlag(long chatId, String externalName) throws SQLException {
        // Маппинг "внешнее имя" → "имя колонки в БД"
        Map<String, String> mapping = Map.of(
                "ai-vs-human",      "ai_check",
                "curse-checking",   "curse_check",
                "easy-ml-checking", "easy_ml_spam_check",
                "erotic-scam",      "erotic_check"
        );

        String columnName = mapping.get(externalName);
        if (columnName == null) {
            throw new IllegalArgumentException("Unsupported column: " + externalName);
        }

        final String sql = "UPDATE chat_settings SET " + columnName + " = NOT " + columnName + " WHERE chat_id = ?";
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, chatId);
            return ps.executeUpdate();
        }
    }

    // Получить только id чатов по user_id
    public List<Long> findIdsByUserId(long userId) throws SQLException {
        final String sql = "SELECT id FROM chat WHERE user_id = ? ORDER BY id";
        List<Long> ids = new ArrayList<>();
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getLong("id"));
            }
        }
        return ids;
    }

    // Получить чаты пользователя с именами
    public List<Chat> findByUserId(long userId) throws SQLException {
        final String sql = "SELECT id, user_id, name FROM chat WHERE user_id = ? ORDER BY id";
        List<Chat> chats = new ArrayList<>();
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chats.add(new Chat(
                            rs.getLong("id"),
                            rs.getLong("user_id"),
                            rs.getString("name")
                    ));
                }
            }
        }
        return chats;
    }
    public Optional<String> findNameById(long chatId) throws SQLException {
        final String sql = "SELECT name FROM chat WHERE id = ?";
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, chatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.ofNullable(rs.getString("name"));
            }
        }
    }
    public HashMap<Long, String> findIdNameByUserId(long userId) throws SQLException {
        final String sql = "SELECT id, name FROM chat WHERE user_id = ?";
        HashMap<Long, String> result = new HashMap<>();
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getLong("id"), rs.getString("name"));
                }
            }
        }
        return result;
    }
    // dao/ChatDao.java
    public int insert(long chatId, long userId, String name) throws SQLException {
        final String sql = "INSERT INTO chat (id, user_id, name) VALUES (?, ?, ?)";
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, chatId);
            ps.setLong(2, userId);
            ps.setString(3, name);
            return ps.executeUpdate(); // 1 — OK
        }
    }

    public int deleteById(long chatId) throws SQLException {
        final String sql1 = "DELETE FROM chat_settings WHERE chat_id = ?";
        final String sql2 = "DELETE FROM chat WHERE id = ?";
        try (Connection c = cp.getConnection()) {
            c.setAutoCommit(false); // начинаем транзакцию
            int deletedSettings;
            int deletedChat;
            try (PreparedStatement ps1 = c.prepareStatement(sql1);
                 PreparedStatement ps2 = c.prepareStatement(sql2)) {

                // сначала удаляем настройки
                ps1.setLong(1, chatId);
                deletedSettings = ps1.executeUpdate();

                // потом удаляем сам чат
                ps2.setLong(1, chatId);
                deletedChat = ps2.executeUpdate();

                c.commit(); // всё прошло успешно → фиксируем транзакцию
            } catch (SQLException e) {
                c.rollback(); // если ошибка → откатываем
                throw e;
            } finally {
                c.setAutoCommit(true); // возвращаем поведение по умолчанию
            }
            return deletedChat; // 1 если чат удалён, 0 если его не было
        }
    }


}

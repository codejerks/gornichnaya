package org.example.dao;

import org.example.domain.ChatSettings;
import org.example.interfac.ConnectionProvider;

import java.sql.*;
import java.util.Optional;
import java.util.Set;

public class ChatSettingsDao {
    private final ConnectionProvider cp;
    public ChatSettingsDao(ConnectionProvider cp) { this.cp = cp; }

    // Получить все настройки по chat_id
    public Optional<ChatSettings> findByChatId(long chatId) throws SQLException {
        final String sql = """
            SELECT chat_id, easy_ml_spam_check, ai_check, erotic_check, curse_check
            FROM chat_settings
            WHERE chat_id = ?
            """;
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, chatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new ChatSettings(
                        rs.getLong("chat_id"),
                        rs.getBoolean("easy_ml_spam_check"),
                        rs.getBoolean("ai_check"),
                        rs.getBoolean("erotic_check"),
                        rs.getBoolean("curse_check")
                ));
            }
        }
    }

    // Обновить ОДИН конкретный флаг по chat_id (белый список колонок)
    public int updateSingleFlag(long chatId, String columnName, boolean value) throws SQLException {
        Set<String> allowed = Set.of("easy_ml_spam_check","ai_check","erotic_check","curse_check");
        if (!allowed.contains(columnName)) {
            throw new IllegalArgumentException("Unsupported column: " + columnName);
        }
        final String sql = "UPDATE chat_settings SET " + columnName + " = ? WHERE chat_id = ?";
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, value);
            ps.setLong(2, chatId);
            return ps.executeUpdate(); // 1 — обновлено; 0 — строки нет
        }
    }

    public int setEasyMlSpamCheck(long chatId, boolean v) throws SQLException {
        return updateSingleFlag(chatId, "easy_ml_spam_check", v);
    }
    public int setAiCheck(long chatId, boolean v) throws SQLException {
        return updateSingleFlag(chatId, "ai_check", v);
    }
    public int setEroticCheck(long chatId, boolean v) throws SQLException {
        return updateSingleFlag(chatId, "erotic_check", v);
    }
    public int setCurseCheck(long chatId, boolean v) throws SQLException {
        return updateSingleFlag(chatId, "curse_check", v);
    }

    // Создать строку настроек с дефолтами, если её нет (вставка по chat_id)
    public int insertDefaultsIfMissing(long chatId) throws SQLException {
        final String sql = """
            INSERT INTO chat_settings (chat_id, easy_ml_spam_check, ai_check, erotic_check, curse_check)
            VALUES (?, FALSE, FALSE, FALSE, FALSE)
            ON CONFLICT (chat_id) DO NOTHING
            """;
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, chatId);
            return ps.executeUpdate(); // 1 — вставлено; 0 — уже существовало
        }
    }
}

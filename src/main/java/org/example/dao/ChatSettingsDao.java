package org.example.dao;

import org.example.domain.ChatSettings;
import org.example.services.ConnectionProvider;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ChatSettingsDao {
    private final ConnectionProvider cp;
    public ChatSettingsDao(ConnectionProvider cp) { this.cp = cp; }

    // Получить все настройки по chat_id



    // Обновить ОДИН конкретный флаг по chat_id (белый список колонок)




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

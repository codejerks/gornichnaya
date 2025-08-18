package org.example.dao;

import org.example.domain.Chat;
import org.example.interfac.ConnectionProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatDao {
    private final ConnectionProvider cp;
    public ChatDao(ConnectionProvider cp) { this.cp = cp; }

    // Добавить чат пользователю
    public void insert(Chat chat) throws SQLException {
        final String sql = "INSERT INTO chat (id, user_id, name) VALUES (?, ?, ?)";
        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, chat.getId());
            ps.setLong(2, chat.getUserId());
            ps.setString(3, chat.getName());
            ps.executeUpdate();
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
}

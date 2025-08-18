package org.example.dao;

import org.example.domain.Users;
import org.example.services.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDao {
    private final ConnectionProvider cp;

    public UserDao(ConnectionProvider cp) {
        this.cp = cp;
    }

    // Добавить нового пользователя (id задаётся извне)
    public void insert(Users user) throws SQLException {
        final String sql =
                "INSERT INTO users (id, username) VALUES (?, ?) " +
                        "ON CONFLICT (id) DO UPDATE SET username = EXCLUDED.username";


        try (Connection c = cp.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.executeUpdate();
        }
    }
}

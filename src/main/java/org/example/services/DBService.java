package org.example.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Микросервис взаимодействия с БД
 * <p>
 *     {@literal @}annoying_rusk: Ничего не менял. Очень сырой, но легко адаптировать
 * </p>
 */
public class DBService {

    private final String url;
    private final String user;
    private final String password;

    public DBService() {
        this.url = "jdbc:postgresql://localhost:5433/tg_bot";
        this.user = "postgres";
        this.password = "0707";
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}


package org.example.services;

import org.example.interfac.ConnectionProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Микросервис взаимодействия с БД
 * <p>
 *     {@literal @}annoying_rusk: Ничего не менял. Очень сырой, но легко адаптировать
 * </p>
 */
public class DBService implements ConnectionProvider {

    private final String url;
    private final String user;
    private final String password;

    public DBService() {
        this.url = "jdbc:postgresql://localhost:5433/mydatabase?sslmode=disable&loginTimeout=5";
        this.user = "myuser";
        this.password = "mypassword";
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}


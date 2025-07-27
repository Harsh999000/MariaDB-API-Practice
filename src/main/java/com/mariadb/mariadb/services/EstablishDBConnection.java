package com.mariadb.mariadb.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EstablishDBConnection {
    @Value("${spring.datasource.url}")
    private String db_connection_url;

    @Value("${spring.datasource.username}")
    private String db_connection_username;

    @Value("${spring.datasource.password}")
    private String db_connection_password;

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(db_connection_url, db_connection_username, db_connection_password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

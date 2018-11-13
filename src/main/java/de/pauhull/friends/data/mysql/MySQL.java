package de.pauhull.friends.data.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends Database {

    private String user;
    private String database;
    private String password;
    private String port;
    private String host;

    public MySQL(String host, String port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    @Override
    public Connection openConnection() throws SQLException {

        if (isConnected())
            return connection;

        String url = "jdbc:mysql://%s:%s/%s?autoReconnect=true";
        url = String.format(url, host, port, database);
        System.out.println(url);
        connection = DriverManager.getConnection(url, this.user, this.password);
        return connection;
    }
}

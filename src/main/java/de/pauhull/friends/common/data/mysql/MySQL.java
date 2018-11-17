package de.pauhull.friends.common.data.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends Database {

    private boolean ssl;
    private String user;
    private String database;
    private String password;
    private String port;
    private String host;

    public MySQL(String host, String port, String database, String user, String password, boolean ssl) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.ssl = ssl;
    }

    @Override
    public Connection openConnection() throws SQLException {

        if (isConnected())
            return connection;

        String url = "jdbc:mysql://%s:%s/%s?autoReconnect=true&useSSL=" + ssl;
        url = String.format(url, host, port, database);
        connection = DriverManager.getConnection(url, this.user, this.password);
        return connection;
    }
}

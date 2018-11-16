package de.pauhull.friends.common.data.mysql;

import lombok.Getter;

import java.sql.*;

public abstract class Database {

    @Getter
    protected Connection connection;

    protected Database() {
        this.connection = null;
    }

    public abstract Connection openConnection() throws SQLException;

    public boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public boolean closeConnection() throws SQLException {

        if (connection == null) {
            return false;
        }

        connection.close();
        return true;
    }

    public ResultSet querySQL(String query) throws SQLException {

        if (!isConnected()) {
            openConnection();
        }

        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);

        return result;
    }

    public int updateSQL(String query) throws SQLException {

        if (!isConnected()) {
            openConnection();
        }

        Statement statement = connection.createStatement();
        int result = statement.executeUpdate(query);

        return result;
    }

    public PreparedStatement prepare(String sql) throws SQLException {

        if (!isConnected()) {
            openConnection();
        }

        PreparedStatement statement = connection.prepareStatement(sql);

        return statement;
    }

}
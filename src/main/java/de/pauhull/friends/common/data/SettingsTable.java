package de.pauhull.friends.common.data;

import de.pauhull.friends.common.data.mysql.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class SettingsTable {

    protected Database database;
    protected ExecutorService executorService;
    protected String table;

    public SettingsTable(Database database, ExecutorService executorService, String tablePrefix) {
        this.database = database;
        this.executorService = executorService;
        this.table = tablePrefix + "settings";

        String createTableQuery = "CREATE TABLE IF NOT EXISTS `%s` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `requests` BOOLEAN, `notifications` BOOLEAN, `jumping` BOOLEAN, `messages` BOOLEAN, `status` VARCHAR(255), PRIMARY KEY (`id`))";
        try {
            database.updateSQL(String.format(createTableQuery, table));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void setStatus(UUID uuid, String status) {
        setValue(uuid, "status", status);
    }

    public void getStatus(UUID uuid, Consumer<String> consumer) {
        getValue(uuid, "status", status -> {
            if (status == null) {
                consumer.accept("§7Ich liebe NovusMC");
            } else {
                consumer.accept((String) status);
            }
        });
    }

    public void setRequests(UUID uuid, boolean requests) {
        setValue(uuid, "requests", requests);
    }

    public void getRequests(UUID uuid, Consumer<Boolean> consumer) {
        getValue(uuid, "requests", requests -> {
            if (requests == null) {
                consumer.accept(true);
            } else {
                consumer.accept((Boolean) requests);
            }
        });
    }

    public void setNotifications(UUID uuid, boolean notifications) {
        setValue(uuid, "notifications", notifications);
    }

    public void getNotifications(UUID uuid, Consumer<Boolean> consumer) {
        getValue(uuid, "notifications", notifications -> {
            if (notifications == null) {
                consumer.accept(true);
            } else {
                consumer.accept((Boolean) notifications);
            }
        });
    }

    public void setMessages(UUID uuid, boolean messages) {
        setValue(uuid, "messages", messages);
    }

    public void getMessages(UUID uuid, Consumer<Boolean> consumer) {
        getValue(uuid, "messages", messages -> {
            if (messages == null) {
                consumer.accept(true);
            } else {
                consumer.accept((Boolean) messages);
            }
        });
    }

    public void setJumping(UUID uuid, boolean jumping) {
        setValue(uuid, "jumping", jumping);
    }

    public void getJumping(UUID uuid, Consumer<Boolean> consumer) {
        getValue(uuid, "jumping", jumping -> {
            if (jumping == null) {
                consumer.accept(true);
            } else {
                consumer.accept((Boolean) jumping);
            }
        });
    }


    public void exists(UUID uuid, Consumer<Boolean> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = database.querySQL(String.format("SELECT * FROM `%s` WHERE `uuid`='%s'", table, uuid.toString()));
                consumer.accept(result.next());

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(false);
            }
        });
    }

    public void createRow(UUID uuid) {
        executorService.execute(() -> {
            try {

                PreparedStatement statement = database.prepare(String.format("INSERT INTO `%s` VALUES (0, ?, true, true, true, true, ?)", table));
                statement.setString(1, uuid.toString());
                statement.setString(2, "§7Ich liebe NovusMC");
                statement.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setValue(UUID uuid, String column, Object value) {
        exists(uuid, exists -> {
            try {

                if (exists) {

                    PreparedStatement statement = database.prepare(String.format("UPDATE `%s` SET `%s`=? WHERE `uuid`=?", table, column));
                    statement.setObject(1, value);
                    statement.setString(2, uuid.toString());
                    statement.execute();

                } else {
                    createRow(uuid);
                    setValue(uuid, column, value);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void getValue(UUID uuid, String column, Consumer<Object> consumer) {
        exists(uuid, exists -> {
            try {

                if (exists) {

                    ResultSet result = database.querySQL(String.format("SELECT * FROM `%s` WHERE `uuid`='%s'", table, uuid.toString()));
                    if (result.next()) {
                        consumer.accept(result.getObject(column));
                    }

                } else {
                    consumer.accept(null);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(null);
            }
        });

    }

}

package de.pauhull.friends.data;

import de.pauhull.friends.Friends;
import de.pauhull.friends.data.mysql.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class SettingsTable {

    private Database database;
    private ExecutorService executorService;
    private String table;

    public SettingsTable(Database database, ExecutorService executorService) {
        this.database = database;
        this.executorService = executorService;
        this.table = Friends.getInstance().getTablePrefix() + "settings";

        //TODO: status

        String createTableQuery = "CREATE TABLE IF NOT EXISTS `%s` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `notifications` BOOLEAN, `jumping` BOOLEAN, `messages` BOOLEAN, PRIMARY KEY (`id`))";
        try {
            database.updateSQL(String.format(createTableQuery, table));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void setNotifications(UUID uuid, boolean notifications) {
        setValue(uuid, "notifications", notifications);
    }

    public void getNotifications(UUID uuid, Consumer<Boolean> consumer) {
        getValue(uuid, "notifications", notifications -> {
            consumer.accept((Boolean) notifications);
        });
    }

    public void setMessages(UUID uuid, boolean messages) {
        setValue(uuid, "messages", messages);
    }

    public void getMessages(UUID uuid, Consumer<Boolean> consumer) {
        getValue(uuid, "messages", messages -> {
            consumer.accept((Boolean) messages);
        });
    }

    public void setJumping(UUID uuid, boolean jumping) {
        setValue(uuid, "jumping", jumping);
    }

    public void getJumping(UUID uuid, Consumer<Boolean> consumer) {
        getValue(uuid, "jumping", jumping -> {
            consumer.accept((Boolean) jumping);
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

                database.updateSQL(String.format("INSERT INTO `%s` VALUES (0, '%s', true, true, true)", table, uuid.toString()));

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setValue(UUID uuid, String column, Object value) {
        exists(uuid, exists -> {
            try {

                if (exists) {

                    database.updateSQL(String.format("UPDATE `%s` SET `%s`=%s WHERE `uuid`='%s'", table, column, value.toString(), uuid.toString()));

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
                        return;
                    }

                    consumer.accept(null);

                } else {
                    consumer.accept(null);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }

}

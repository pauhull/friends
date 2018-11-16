package de.pauhull.friends.common.data;

import de.pauhull.friends.common.data.mysql.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class LastOnlineTable {

    protected Database database;
    protected ExecutorService executorService;
    protected String table;

    public LastOnlineTable(Database database, ExecutorService executorService, String tablePrefix) {
        this.database = database;
        this.executorService = executorService;
        this.table = tablePrefix + "last_online";

        String createTableQuery = "CREATE TABLE IF NOT EXISTS `%s` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `last_online` BIGINT, PRIMARY KEY (`id`))";
        try {
            database.updateSQL(String.format(createTableQuery, table));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void getLastOnline(UUID uuid, Consumer<Long> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = database.querySQL(String.format("SELECT * FROM `%s` WHERE `uuid`='%s'", table, uuid.toString()));

                if (result.next()) {
                    consumer.accept(result.getLong("last_online"));
                    return;
                }

                consumer.accept(null);

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(null);
            }
        });
    }

    public void setLastOnline(UUID uuid) {
        getLastOnline(uuid, lastOnline -> {
            try {

                if (lastOnline == null) {
                    database.updateSQL(String.format("INSERT INTO `%s` VALUES (0, '%s', %s)", table, uuid.toString(), Long.toString(System.currentTimeMillis())));
                } else {
                    database.updateSQL(String.format("UPDATE `%s` SET `last_online`=%s WHERE `uuid`='%s'", table, Long.toString(System.currentTimeMillis()), uuid.toString()));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}

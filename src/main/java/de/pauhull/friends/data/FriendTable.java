package de.pauhull.friends.data;

import de.pauhull.friends.Friends;
import de.pauhull.friends.data.mysql.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class FriendTable {

    private Database database;
    private ExecutorService executorService;
    private String table;

    public FriendTable(Database database, ExecutorService executorService) {
        this.database = database;
        this.executorService = executorService;
        this.table = Friends.getInstance().getTablePrefix() + "table";

        String createTableQuery = "CREATE TABLE IF NOT EXISTS `%s` (`id` INT AUTO_INCREMENT, `a` VARCHAR(255), `b` VARCHAR(255), `time` BIGINT, PRIMARY KEY (`id`))";
        try {
            database.updateSQL(String.format(createTableQuery, table));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void addFriends(UUID a, UUID b) {
        areFriends(a, b, alreadyFriends -> {
            try {

                if (!alreadyFriends) {
                    database.updateSQL(String.format("INSERT INTO `%s` VALUES (0, '%s', '%s', %s)", table, a.toString(), b.toString(), Long.toString(System.currentTimeMillis())));
                } else {
                    database.updateSQL(String.format("UPDATE `%s` SET `time`=%s WHERE (`a`='%s' OR `b`='%s') AND (`a`='%s' OR `b`='%s')",
                            table, Long.toString(System.currentTimeMillis()), a.toString(), a.toString(), b.toString(), b.toString()));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeFriends(UUID a, UUID b) {
        areFriends(a, b, areFriends -> {
            try {

                if (areFriends) {
                    database.updateSQL(String.format("DELETE FROM `%s` WHERE (`a`='%s' OR `b`='%s') AND (`a`='%s' OR `b`='%s')",
                            table, a.toString(), a.toString(), b.toString(), b.toString()));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void areFriends(UUID a, UUID b, Consumer<Boolean> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = database.querySQL(String.format("SELECT * FROM `%s` WHERE (`a`='%s' OR `b`='%s') AND (`a`='%s' OR `b`='%s')",
                        table, a.toString(), a.toString(), b.toString(), b.toString()));

                consumer.accept(result.next());

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(false);
            }
        });
    }

}

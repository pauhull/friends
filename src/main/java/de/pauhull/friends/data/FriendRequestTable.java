package de.pauhull.friends.data;

import de.pauhull.friends.Friends;
import de.pauhull.friends.data.mysql.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class FriendRequestTable {

    private Database database;
    private ExecutorService executorService;
    private String table;

    public FriendRequestTable(Database database, ExecutorService executorService) {
        this.database = database;
        this.executorService = executorService;
        this.table = Friends.getInstance().getTablePrefix() + "requests";

        String createTableQuery = "CREATE TABLE IF NOT EXISTS `%s` (`id` INT AUTO_INCREMENT, `from` VARCHAR(255), `to` VARCHAR(255), `time` BIGINT, PRIMARY KEY (`id`))";
        try {
            database.updateSQL(String.format(createTableQuery, table));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void getTime(UUID from, UUID to, Consumer<Long> consumer) {
        executorService.execute(() -> {
            try {
                ResultSet result = database.querySQL(String.format("SELECT * FROM `%s` WHERE `from`='%s' AND `to`='%s'",
                        table, from.toString(), to.toString()));

                if (result.next()) {
                    consumer.accept(result.getLong("time"));
                } else {
                    consumer.accept(null);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(null);
            }
        });
    }

    public void acceptFriendRequest(UUID from, UUID to) {
        executorService.execute(() -> {
            try {

                database.updateSQL(String.format("DELETE FROM `%s` WHERE `from`='%s' AND `to`='%s'", table, from.toString(), to.toString()));
                Friends.getInstance().getFriendTable().addFriends(from, to);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void denyFriendRequest(UUID from, UUID to) {
        executorService.execute(() -> {
            try {

                database.updateSQL(String.format("DELETE FROM `%s` WHERE `from`='%s' AND `to`='%s'", table, from.toString(), to.toString()));

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void getOpenFriendRequests(UUID to, Consumer<Integer> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = database.querySQL(String.format("SELECT * FROM `%s` WHERE `to`='%s'", table, to.toString()));

                int results = 0;
                while (result.next())
                    results++;

                consumer.accept(results);
            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(0);
            }
        });
    }

    public void sendFriendRequest(UUID from, UUID to) {
        getTime(from, to, time -> {
            try {
                if (time == null) {
                    database.updateSQL(String.format("INSERT INTO `%s` VALUES (0, '%s', '%s', %s)",
                            table, from.toString(), to.toString(), Long.toString(System.currentTimeMillis())));
                } else {
                    database.updateSQL(String.format("UPDATE `%s` SET `time`=%s WHERE `from`='%s' AND `to`='%s'",
                            table, Long.toString(System.currentTimeMillis()), from.toString(), to.toString()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}

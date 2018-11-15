package de.pauhull.friends.data;

import de.pauhull.friends.Friends;
import de.pauhull.friends.data.mysql.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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

                if (a.equals(b)) {
                    consumer.accept(false);
                    return;
                }

                ResultSet result = database.querySQL(String.format("SELECT * FROM `%s` WHERE (`a`='%s' OR `b`='%s') AND (`a`='%s' OR `b`='%s')",
                        table, a.toString(), a.toString(), b.toString(), b.toString()));

                consumer.accept(result.next());

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(false);
            }
        });
    }

    public void getFriends(UUID uuid, Consumer<Collection<ProxiedPlayer>> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = database.querySQL(String.format("SELECT * FROM `%s` WHERE `a`='%s' OR `b`='%s'", table, uuid.toString(), uuid.toString()));

                Set<ProxiedPlayer> players = new HashSet<>();
                while (result.next()) {
                    UUID a = UUID.fromString(result.getString("a"));
                    UUID b = UUID.fromString(result.getString("b"));
                    ProxiedPlayer player;

                    if (a.equals(uuid)) {
                        player = ProxyServer.getInstance().getPlayer(b);
                    } else {
                        player = ProxyServer.getInstance().getPlayer(a);
                    }

                    if (player != null) {
                        players.add(player);
                    }
                }

                consumer.accept(players);

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(new HashSet<>());
            }
        });
    }

}

package de.pauhull.friends.bungee.data;

import de.pauhull.friends.common.data.FriendRequestTable;
import de.pauhull.friends.common.data.FriendTable;
import de.pauhull.friends.common.data.mysql.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class BungeeFriendRequestTable extends FriendRequestTable {

    public BungeeFriendRequestTable(Database database, ExecutorService executorService, String tablePrefix) {
        super(database, executorService, tablePrefix);
    }

    public void acceptAll(FriendTable friendTable, UUID to, Consumer<Collection<ProxiedPlayer>> consumer) {
        executorService.execute(() -> {
            try {
                Collection<ProxiedPlayer> players = new HashSet<>();

                ResultSet requests = database.querySQL(String.format("SELECT * FROM `%s` WHERE `to`='%s'", table, to.toString()));
                while (requests.next()) {
                    UUID from = UUID.fromString(requests.getString("from"));
                    acceptFriendRequest(friendTable, from, to);
                    players.add(ProxyServer.getInstance().getPlayer(from));
                }

                consumer.accept(players);
            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(new HashSet<>());
            }
        });
    }

    public void denyAll(UUID to, Consumer<Collection<ProxiedPlayer>> consumer) {
        executorService.execute(() -> {
            try {
                Collection<ProxiedPlayer> players = new HashSet<>();

                ResultSet requests = database.querySQL(String.format("SELECT * FROM `%s` WHERE `to`='%s'", table, to.toString()));
                while (requests.next()) {
                    UUID from = UUID.fromString(requests.getString("from"));
                    denyFriendRequest(from, to);
                    players.add(ProxyServer.getInstance().getPlayer(from));
                }

                consumer.accept(players);
            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(new HashSet<>());
            }
        });
    }

    public void isRequested(UUID from, UUID to, Consumer<Boolean> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = database.querySQL(String.format("SELECT * FROM `%s` WHERE `from`='%s' AND `to`='%s'", table, from.toString(), to.toString()));
                consumer.accept(result.next());

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(false);
            }
        });
    }

    public void removeRequest(UUID from, UUID to) {
        executorService.execute(() -> {
            try {

                database.updateSQL(String.format("DELETE FROM `%s` WHERE `from`='%s' AND `to`='%s'", table, from.toString(), to.toString()));

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}

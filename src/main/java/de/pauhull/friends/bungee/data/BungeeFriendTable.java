package de.pauhull.friends.bungee.data;

import de.pauhull.friends.common.data.FriendTable;
import de.pauhull.friends.common.data.mysql.Database;
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

public class BungeeFriendTable extends FriendTable {

    public BungeeFriendTable(Database database, ExecutorService executorService, String tablePrefix) {
        super(database, executorService, tablePrefix);
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

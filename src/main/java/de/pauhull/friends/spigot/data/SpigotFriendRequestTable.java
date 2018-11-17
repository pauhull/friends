package de.pauhull.friends.spigot.data;

import de.pauhull.friends.common.data.FriendRequestTable;
import de.pauhull.friends.common.data.mysql.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class SpigotFriendRequestTable extends FriendRequestTable {

    public SpigotFriendRequestTable(Database database, ExecutorService executorService, String tablePrefix) {
        super(database, executorService, tablePrefix);
    }

    public void getFriendRequests(UUID to, int start, int results, Consumer<Map<UUID, Long>> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = database.querySQL(String.format("SELECT * FROM `%s` WHERE `to`='%s' ORDER BY `time` DESC LIMIT %s, %s",
                        table, to.toString(), Integer.toString(start), Integer.toString(results)));
                Map<UUID, Long> requests = new HashMap<>();
                while (result.next()) {
                    requests.put(UUID.fromString(result.getString("from")), result.getLong("time"));
                }
                consumer.accept(requests);

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(new HashMap<>());
            }
        });
    }


}

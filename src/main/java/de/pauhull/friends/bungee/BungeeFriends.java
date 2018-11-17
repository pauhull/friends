package de.pauhull.friends.bungee;

import com.ikeirnez.pluginmessageframework.PacketManager;
import com.ikeirnez.pluginmessageframework.implementations.BungeeCordPacketManager;
import de.pauhull.friends.bungee.command.FriendCommand;
import de.pauhull.friends.bungee.command.MsgCommand;
import de.pauhull.friends.bungee.command.ReplyCommand;
import de.pauhull.friends.bungee.data.BungeeFriendRequestTable;
import de.pauhull.friends.bungee.data.BungeeFriendTable;
import de.pauhull.friends.bungee.listener.PlayerDisconnectListener;
import de.pauhull.friends.bungee.listener.PostLoginListener;
import de.pauhull.friends.bungee.util.BungeeUUIDFetcher;
import de.pauhull.friends.bungee.util.IncomingPacketHandler;
import de.pauhull.friends.bungee.util.MessageManager;
import de.pauhull.friends.common.data.LastOnlineTable;
import de.pauhull.friends.common.data.SettingsTable;
import de.pauhull.friends.common.data.mysql.Database;
import de.pauhull.friends.common.data.mysql.MySQL;
import de.pauhull.friends.common.util.FriendThreadFactory;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BungeeFriends extends Plugin {

    //TODO öffentliche parties (premium)

    @Getter
    private static BungeeFriends instance;

    @Getter
    private static String prefix;

    @Getter
    private Database database;

    @Getter
    private ExecutorService executorService;

    @Getter
    private MessageManager messages;

    @Getter
    private String tablePrefix;

    @Getter
    private File configFile;

    @Getter
    private File messageFile;

    @Getter
    private Configuration config;

    @Getter
    private Configuration messageConfig;

    @Getter
    private BungeeUUIDFetcher uuidFetcher;

    @Getter
    private BungeeFriendRequestTable friendRequestTable;

    @Getter
    private BungeeFriendTable friendTable;

    @Getter
    private SettingsTable settingsTable;

    @Getter
    private LastOnlineTable lastOnlineTable;

    @Getter
    private PacketManager packetManager;

    @Override
    public void onEnable() {
        instance = this;

        this.configFile = new File(getDataFolder(), "config.yml");
        this.messageFile = new File(getDataFolder(), "messages.yml");
        this.config = copyAndLoad("config.yml", configFile);
        this.messageConfig = copyAndLoad("messages.yml", messageFile);
        this.messages = new MessageManager().load(this.messageConfig);
        prefix = messages.getPrefix();
        this.tablePrefix = config.getString("Database.TablePrefix");
        this.executorService = Executors.newSingleThreadExecutor(new FriendThreadFactory("BungeeFriend"));
        this.uuidFetcher = new BungeeUUIDFetcher(executorService);
        this.database = new MySQL(config.getString("Database.MySQL.Host"),
                config.getString("Database.MySQL.Port"),
                config.getString("Database.MySQL.Database"),
                config.getString("Database.MySQL.User"),
                config.getString("Database.MySQL.Password"),
                config.getBoolean("Database.MySQL.SSL"));

        try {
            this.database.openConnection();
        } catch (SQLException e) {
            ProxyServer.getInstance().getLogger().severe("[Friends] Couldn't connect to MySQL!");
            return;
        }

        this.friendRequestTable = new BungeeFriendRequestTable(database, executorService, tablePrefix);
        this.friendTable = new BungeeFriendTable(database, executorService, tablePrefix);
        this.settingsTable = new SettingsTable(database, executorService, tablePrefix);
        this.lastOnlineTable = new LastOnlineTable(database, executorService, tablePrefix);
        this.packetManager = new BungeeCordPacketManager(this, "Friends");
        this.packetManager.registerListener(new IncomingPacketHandler());

        FriendCommand.register();
        MsgCommand.register();
        PlayerDisconnectListener.register();
        ReplyCommand.register();
        PostLoginListener.register();
    }

    @Override
    public void onDisable() {
        try {
            this.database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.executorService.shutdown();
    }

    private Configuration copyAndLoad(String resource, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                Files.copy(getResourceAsStream(resource), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void reload() {
        this.messageConfig = copyAndLoad("messages.yml", messageFile);
        this.messages.load(messageConfig);
    }

}

package de.pauhull.friends;

import de.pauhull.friends.command.FriendCommand;
import de.pauhull.friends.data.FriendRequestTable;
import de.pauhull.friends.data.mysql.Database;
import de.pauhull.friends.data.mysql.MySQL;
import de.pauhull.friends.util.CachedUUIDFetcher;
import de.pauhull.friends.util.FriendThreadFactory;
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

public class Friends extends Plugin {

    @Getter
    private static Friends instance;

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
    private CachedUUIDFetcher uuidFetcher;

    @Getter
    private FriendRequestTable friendRequestTable;

    @Override
    public void onEnable() {
        instance = this;

        this.configFile = new File(getDataFolder(), "config.yml");
        this.messageFile = new File(getDataFolder(), "messages.yml");
        this.config = copyAndLoad("config.yml", configFile);
        this.messageConfig = copyAndLoad("messages.yml", messageFile);
        this.executorService = Executors.newSingleThreadExecutor(new FriendThreadFactory());
        this.messages = new MessageManager().load(this.messageConfig);
        prefix = messages.getPrefix();
        this.uuidFetcher = new CachedUUIDFetcher(executorService);
        this.tablePrefix = config.getString("Database.TablePrefix");
        this.database = new MySQL(config.getString("Database.MySQL.Host"),
                config.getString("Database.MySQL.Port"),
                config.getString("Database.MySQL.Database"),
                config.getString("Database.MySQL.User"),
                config.getString("Database.MySQL.Password"));

        try {
            this.database.openConnection();
        } catch (SQLException e) {
            ProxyServer.getInstance().getLogger().severe("Couldn't connect to MySQL! Shutting down...");
            return;
        }

        this.friendRequestTable = new FriendRequestTable(database, executorService);

        FriendCommand.register();
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

}
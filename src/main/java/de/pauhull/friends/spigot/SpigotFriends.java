package de.pauhull.friends.spigot;

import de.pauhull.friends.common.data.LastOnlineTable;
import de.pauhull.friends.common.data.SettingsTable;
import de.pauhull.friends.common.data.mysql.Database;
import de.pauhull.friends.common.data.mysql.MySQL;
import de.pauhull.friends.common.util.FriendThreadFactory;
import de.pauhull.friends.spigot.command.TestCommand;
import de.pauhull.friends.spigot.data.SpigotFriendTable;
import de.pauhull.friends.spigot.inventory.MainMenu;
import de.pauhull.friends.spigot.util.HeadCache;
import de.pauhull.friends.spigot.util.SpigotUUIDFetcher;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpigotFriends extends JavaPlugin {

    @Getter
    private static SpigotFriends instance;

    @Getter
    private static MainMenu mainMenu = null;

    @Getter
    private File configFile;

    @Getter
    private FileConfiguration config;

    @Getter
    private Database mysql;

    @Getter
    private SpigotFriendTable friendTable;

    @Getter
    private SettingsTable settingsTable;

    @Getter
    private ExecutorService executorService;

    @Getter
    private String tablePrefix;

    @Getter
    private SpigotUUIDFetcher uuidFetcher;

    @Getter
    private HeadCache headCache;

    @Getter
    private LastOnlineTable lastOnlineTable;

    @Override
    public void onEnable() {
        instance = this;

        this.executorService = Executors.newSingleThreadExecutor(new FriendThreadFactory("SpigotFriends"));
        this.configFile = new File(getDataFolder(), "config.yml");
        this.config = copyAndLoad("config.yml", configFile);
        this.tablePrefix = config.getString("Database.TablePrefix");
        this.mysql = new MySQL(config.getString("Database.MySQL.Host"),
                config.getString("Database.MySQL.Port"),
                config.getString("Database.MySQL.Database"),
                config.getString("Database.MySQL.User"),
                config.getString("Database.MySQL.Password"));
        this.uuidFetcher = new SpigotUUIDFetcher(executorService);
        this.headCache = new HeadCache();

        try {
            this.mysql.openConnection();
        } catch (SQLException e) {
            System.err.println("[Friends] Couldn't connect to MySQL!");
            return;
        }

        mainMenu = new MainMenu(this);

        this.settingsTable = new SettingsTable(mysql, executorService, tablePrefix);
        this.friendTable = new SpigotFriendTable(mysql, executorService, tablePrefix);
        this.lastOnlineTable = new LastOnlineTable(mysql, executorService, tablePrefix);

        new TestCommand(this);

    }

    @Override
    public void onDisable() {
        this.executorService.shutdown();
    }

    private FileConfiguration copyAndLoad(String resource, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                Files.copy(getResource(resource), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

}

package de.pauhull.friends.spigot.command;

import de.pauhull.friends.spigot.SpigotFriends;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {

    private SpigotFriends friends;

    public TestCommand(SpigotFriends friends) {
        this.friends = friends;

        friends.getCommand("test").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("test")) {
            Player player = (Player) sender;
            SpigotFriends.getMainMenu().show(player, 0);
        }

        return true;
    }

}

package de.pauhull.friends.command;

import de.pauhull.friends.Friends;
import de.pauhull.friends.command.subcommand.AddSubCommand;
import de.pauhull.friends.command.subcommand.RemoveSubCommand;
import de.pauhull.friends.command.subcommand.SubCommand;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;

public class FriendCommand extends Command {

    @Getter
    private static List<SubCommand> subCommands = new ArrayList<>();

    static {
        subCommands.add(new AddSubCommand());
        subCommands.add(new RemoveSubCommand());
    }

    public FriendCommand() {
        super("friend");
    }

    public static void register() {
        ProxyServer.getInstance().getPluginManager().registerCommand(Friends.getInstance(), new FriendCommand());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length > 0) {
            for (SubCommand command : subCommands) {
                if (!args[0].equalsIgnoreCase(command.getName()))
                    continue;

                command.execute(sender, args);
            }
        }

    }

}

package de.pauhull.friends.command;

import com.google.common.collect.ImmutableSet;
import de.pauhull.friends.Friends;
import de.pauhull.friends.command.subcommand.*;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendCommand extends Command implements TabExecutor {

    @Getter
    private static List<SubCommand> subCommands = new ArrayList<>();

    static {
        subCommands.add(new AcceptDenySubCommand());
        subCommands.add(new AddSubCommand());
        subCommands.add(new RemoveSubCommand());
        subCommands.add(new ToggleSubCommand());
        subCommands.add(new JumpSubCommand());
    }

    //private Friends friends;
    public FriendCommand(Friends friends) {
        super("friend");
        //this.friends = friends;
        friends.getProxy().getPluginManager().registerCommand(friends, this);
    }

    public static void register() {
        new FriendCommand(Friends.getInstance());
    }

    public static void registerReload() {
        subCommands.add(new ReloadSubCommand());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length > 0) {
            for (SubCommand command : subCommands) {
                for (String name : command.getNames()) {
                    if (name.equalsIgnoreCase(args[0])) {
                        command.execute(sender, args);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 0) {
            for (SubCommand command : subCommands) {
                for (int i = 0; i < command.getNames().length; i++) {
                    String name = command.getNames()[i];

                    if (name.equalsIgnoreCase(args[0])) {
                        if (i < command.getTabPermissions().length && command.getTabPermissions()[i] != null) {
                            if (!sender.hasPermission(command.getTabPermissions()[i])) {
                                return ImmutableSet.of();
                            }
                        }

                        return command.onTabComplete(sender, args);
                    }
                }
            }
        }

        Set<String> matches = new HashSet<>();
        String search = args[0].toLowerCase();
        if (args.length == 1) {
            for (SubCommand command : subCommands) {
                for (int i = 0; i < command.getNames().length; i++) {
                    String name = command.getNames()[i];

                    if (name.toLowerCase().startsWith(search)) {
                        if (i < command.getTabPermissions().length && command.getTabPermissions()[i] != null) {
                            if (!sender.hasPermission(command.getTabPermissions()[i])) {
                                continue;
                            }
                        }

                        matches.add(name);
                    }
                }
            }
        }

        return matches;
    }

}

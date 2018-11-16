package de.pauhull.friends.bungee.command;

import com.google.common.collect.ImmutableSet;
import de.pauhull.friends.bungee.Friends;
import de.pauhull.friends.bungee.command.subcommand.*;
import de.pauhull.friends.common.util.Permissions;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
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
        subCommands.add(new ReloadSubCommand());
        subCommands.add(new RemoveSubCommand());
        subCommands.add(new ToggleSubCommand());
        subCommands.add(new JumpSubCommand());
        subCommands.add(new StatusSubCommand());
    }

    //private Friends friends;
    public FriendCommand(Friends friends) {
        super("friend", null, "friends");
        //this.friends = friends;
        friends.getProxy().getPluginManager().registerCommand(friends, this);
    }

    public static void register() {
        new FriendCommand(Friends.getInstance());
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
        } else {
            if (sender.hasPermission(Permissions.ACCEPT))
                sender.sendMessage(TextComponent.fromLegacyText("§8» §e/friend accept <Spieler> §8×§7 Freundschaftsanfrage akzeptieren"));

            if (sender.hasPermission(Permissions.ACCEPT_ALL))
                sender.sendMessage(TextComponent.fromLegacyText("§8» §e/friend accept all §8×§7 Alle Freundschaftsanfragen akzeptieren"));

            if (sender.hasPermission(Permissions.DENY))
                sender.sendMessage(TextComponent.fromLegacyText("§8» §e/friend deny <Spieler> §8×§7 Freundschaftsanfrage ablehnen"));

            if (sender.hasPermission(Permissions.ACCEPT_ALL))
                sender.sendMessage(TextComponent.fromLegacyText("§8» §e/friend deny all §8×§7 Alle Freundschaftsanfragen ablehnen"));

            if (sender.hasPermission(Permissions.ADD))
                sender.sendMessage(TextComponent.fromLegacyText("§8» §e/friend add <Spieler> §8×§7 Freund hinzufügen"));

            if (sender.hasPermission(Permissions.JUMP))
                sender.sendMessage(TextComponent.fromLegacyText("§8» §e/friend jump <Spieler> §8×§7 Zu Freund springen"));

            if (sender.hasPermission(Permissions.RELOAD))
                sender.sendMessage(TextComponent.fromLegacyText("§8» §e/friend reload §8×§7 Nachrichten neu laden"));

            if (sender.hasPermission(Permissions.REMOVE))
                sender.sendMessage(TextComponent.fromLegacyText("§8» §e/friend remove <Spieler> §8×§7 Freund entfernen"));

            if (sender.hasPermission(Permissions.STATUS))
                sender.sendMessage(TextComponent.fromLegacyText("§8» §e/friend status <Status...> §8×§7 Status setzen"));

            if (sender.hasPermission(Permissions.TOGGLE))
                sender.sendMessage(TextComponent.fromLegacyText("§8» §e/friend toggle §8×§7 Freundschaftsanfragen an- und ausstellen"));

            if (sender.hasPermission(Permissions.TOGGLE))
                sender.sendMessage(TextComponent.fromLegacyText("§8» §e/friend togglemsg §8×§7 Privatnachrichten an- und ausstellen"));

            if (sender.hasPermission(Permissions.TOGGLE))
                sender.sendMessage(TextComponent.fromLegacyText("§8» §e/friend togglenotify §8×§7 Online/Offline-Nachrichten an- und ausstellen"));

            if (sender.hasPermission(Permissions.TOGGLE))
                sender.sendMessage(TextComponent.fromLegacyText("§8» §e/friend togglejump §8×§7 Nachjoinen an- und ausstellen"));
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

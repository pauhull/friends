package de.pauhull.friends.command.subcommand;

import de.pauhull.friends.Friends;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class RemoveSubCommand extends SubCommand {

    private Friends friends;

    public RemoveSubCommand() {
        super("remove");
        this.friends = Friends.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "§c/friend add <Spieler>"));
            return;
        }

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getOnlyPlayers()));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        String removeName = args[1];

    }

}

package de.pauhull.friends.bungee.command.subcommand;

import de.pauhull.friends.bungee.BungeeFriends;
import de.pauhull.friends.common.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class ReloadSubCommand extends SubCommand {

    private BungeeFriends friends;

    public ReloadSubCommand() {
        super("reload");
        this.friends = BungeeFriends.getInstance();
        this.setTabPermissions(Permissions.RELOAD);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.RELOAD)) {
            sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        friends.reload();

        sender.sendMessage(TextComponent.fromLegacyText(BungeeFriends.getPrefix() + "Config reloaded"));

    }

}

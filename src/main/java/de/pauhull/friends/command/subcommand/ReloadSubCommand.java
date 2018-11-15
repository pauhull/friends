package de.pauhull.friends.command.subcommand;

import de.pauhull.friends.Friends;
import de.pauhull.friends.util.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class ReloadSubCommand extends SubCommand {

    private Friends friends;

    public ReloadSubCommand() {
        super("reload");
        this.friends = Friends.getInstance();
        this.setTabPermissions(Permissions.RELOAD);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.RELOAD)) {
            sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + friends.getMessages().getNoPermissions()));
            return;
        }

        friends.reload();
        friends.reloadDatabase();

        sender.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + "Config reloaded"));

    }

}

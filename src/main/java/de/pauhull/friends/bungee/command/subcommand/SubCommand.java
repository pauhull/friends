package de.pauhull.friends.bungee.command.subcommand;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;

public abstract class SubCommand {

    @Getter
    protected String[] names;

    @Getter
    protected String[] tabPermissions = new String[0];

    public SubCommand(String... names) {
        this.names = names;
    }

    public abstract void execute(CommandSender sender, String[] args);

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableSet.of();
    }

    protected void setTabPermissions(String... tabPermissions) {
        this.tabPermissions = tabPermissions;
    }

}

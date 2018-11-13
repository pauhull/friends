package de.pauhull.friends.command.subcommand;

import lombok.Getter;
import net.md_5.bungee.api.CommandSender;

public abstract class SubCommand {

    @Getter
    protected String[] names;

    public SubCommand(String... names) {
        this.names = names;
    }

    public abstract void execute(CommandSender sender, String[] args);

}

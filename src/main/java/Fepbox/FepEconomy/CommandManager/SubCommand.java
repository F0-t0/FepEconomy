package Fepbox.FepEconomy.CommandManager;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    public abstract String getName();
    public abstract String getDescription();
    public abstract String getSyntax();
    public abstract void preform(CommandSender sender, String[] args);
}

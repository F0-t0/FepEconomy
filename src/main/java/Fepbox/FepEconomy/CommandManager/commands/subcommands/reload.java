package Fepbox.FepEconomy.CommandManager.commands.subcommands;

import Fepbox.FepEconomy.CommandManager.SubCommand;
import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import org.bukkit.command.CommandSender;

public class reload extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload config, messages and save task";
    }

    @Override
    public String getSyntax() {
        return "/eco reload";
    }

    @Override
    public void preform(CommandSender sender, String[] args) {
        FepEconomy.getPlugin().reloadConfig();
        FepEconomy.getPlugin().reloadMessages();
        FepEconomy.getPlugin().getVaultEconomy().loadCache();
        FepEconomy.getPlugin().restartSaveTask();

        String msg = FepEconomy.getMessagesCfg().getString("reload", "&aConfiguration reloaded successfully");
        sender.sendMessage(ColorUtils.translateColorCodes(msg));
    }
}

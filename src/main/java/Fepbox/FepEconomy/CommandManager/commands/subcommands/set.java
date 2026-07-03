package Fepbox.FepEconomy.CommandManager.commands.subcommands;

import Fepbox.FepEconomy.CommandManager.SubCommand;
import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import Fepbox.FepEconomy.VaultEconomy;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;


public class set extends SubCommand {
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "set the balance of a player";
    }

    @Override
    public String getSyntax() {
        return "/eco set <player> <amount>";
    }

    @Override
    public void preform(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ColorUtils.translateColorCodes(FepEconomy.getMessagesCfg().getString("args",
                    "&cNot enough arguments")));
            return;
        }
        VaultEconomy econ = FepEconomy.getPlugin().getVaultEconomy();
        OfflinePlayer target = FepEconomy.getOfflinePlayerByName(args[1]);
        if (!econ.hasAccount(target)) {
            sender.sendMessage(ColorUtils.translateColorCodes(
                    FepEconomy.getMessagesCfg().getString("player-not-found", "&cCould not find player %player%")
                            .replace("%player%", args[1])));
            return;
        }
        double amount = FepEconomy.parseAmount(args[2]);
        if (Double.isNaN(amount)) {
            sender.sendMessage(ColorUtils.translateColorCodes(
                    FepEconomy.getMessagesCfg().getString("invalid-number", "&cInvalid number format")));
            return;
        }
        econ.setBalance(target, amount);

        String msg = FepEconomy.getMessagesCfg().getString("set",
                "&aSet %player%'s balance to %amount%");
        msg = msg.replace("%amount%", econ.format(amount));
        msg = msg.replace("%player%", args[1]);
        msg = ColorUtils.translateColorCodes(msg);
        sender.sendMessage(msg);
    }
}

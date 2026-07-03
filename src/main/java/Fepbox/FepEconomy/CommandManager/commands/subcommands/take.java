package Fepbox.FepEconomy.CommandManager.commands.subcommands;

import Fepbox.FepEconomy.CommandManager.SubCommand;
import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class take extends SubCommand {
    @Override
    public String getName() {
        return "take";
    }

    @Override
    public String getDescription() {
        return "Takes money";
    }

    @Override
    public String getSyntax() {
        return "/eco take <player> <amount>";
    }

    @Override
    public void preform(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ColorUtils.translateColorCodes(FepEconomy.getMessagesCfg().getString("args",
                    "&cNot enough arguments")));
            return;
        }
        Economy econ = FepEconomy.getPlugin().getVaultEconomy();
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
        econ.withdrawPlayer(target, amount);

        String msg = FepEconomy.getMessagesCfg().getString("take",
                "&aTook %amount% from %player%'s balance");
        msg = msg.replace("%amount%", econ.format(amount));
        msg = msg.replace("%player%", args[1]);
        msg = ColorUtils.translateColorCodes(msg);
        sender.sendMessage(msg);
    }
}

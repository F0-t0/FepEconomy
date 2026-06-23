package Fepbox.FepEconomy.CommandManager.commands.subcommands;

import Fepbox.FepEconomy.CommandManager.SubCommand;
import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class add extends SubCommand {
    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "Add money to player";
    }

    @Override
    public String getSyntax() {
        return "/eco add <player> <number>";
    }

    @Override
    public void preform(Player p, String[] args) {
        if (args.length < 3) {
            p.sendMessage(ColorUtils.translateColorCodes(FepEconomy.getMessagesCfg().getString("args",
                    "&cNot enough arguments")));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        Economy econ = FepEconomy.getPlugin().getVaultEconomy();
        double amount = FepEconomy.parseAmount(args[2]);
        econ.depositPlayer((OfflinePlayer) target, amount);

        String msg = FepEconomy.getMessagesCfg().getString("add",
                "&aAdded %amount% to %player%'s balance");
        msg = msg.replace("%amount%", econ.format(amount));
        msg = msg.replace("%player%", args[1]);
        msg = ColorUtils.translateColorCodes(msg);
        p.sendMessage(msg);
    }
}

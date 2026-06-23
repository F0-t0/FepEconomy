package Fepbox.FepEconomy.CommandManager.commands.subcommands;

import Fepbox.FepEconomy.CommandManager.SubCommand;
import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
    public void preform(Player p, String[] args) {
        if (args.length < 3) {
            p.sendMessage(ColorUtils.translateColorCodes(FepEconomy.getMessagesCfg().getString("args",
                    "&cNot enough arguments")));
            return;
        }
        Economy econ = FepEconomy.getPlugin().getVaultEconomy();
        double amount = FepEconomy.parseAmount(args[2]);
        econ.withdrawPlayer((OfflinePlayer) Bukkit.getPlayer(args[1]), amount);

        String msg = FepEconomy.getMessagesCfg().getString("take",
                "&aTook %amount% from %player%'s balance");
        msg = msg.replace("%amount%", econ.format(amount));
        msg = msg.replace("%player%", args[1]);
        msg = ColorUtils.translateColorCodes(msg);
        p.sendMessage(msg);
    }
}

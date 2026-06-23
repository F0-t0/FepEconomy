package Fepbox.FepEconomy.Comands;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class balCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Economy econ = FepEconomy.getPlugin().getVaultEconomy();
        String msg = (args.length < 1) ? FepEconomy.getMessagesCfg().getString("balance-self") : FepEconomy.getMessagesCfg().getString("balance-other");
        if (args.length >= 1) {
            msg = msg.replace("%player%", args[0]);
            msg = msg.replace("%bal%", String.valueOf(econ.getBalance((OfflinePlayer) Bukkit.getPlayer(args[0]))));
        } else {
            msg = msg.replace("%bal%", String.valueOf(econ.getBalance((OfflinePlayer) sender)));
        }
        msg = ColorUtils.translateColorCodes(msg);
        sender.sendMessage(msg);
        return true;
    }
}

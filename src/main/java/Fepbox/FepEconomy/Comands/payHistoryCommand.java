package Fepbox.FepEconomy.Comands;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.MenuManager.menus.PayHistory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class payHistoryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player p) {
            if (args.length == 0) {
                new PayHistory(FepEconomy.getDataManger((Player) sender), 1, null).open();
            } else {
                if (!p.hasPermission("fepeconomy.admin")) {
                    new PayHistory(FepEconomy.getDataManger((Player) sender), 1, null).open();
                }
                String playerName = args[0];
                new PayHistory(FepEconomy.getDataManger((Player) sender), 1, (Player) FepEconomy.getOfflinePlayerByName(playerName)).open();
            }
        }



        return true;
    }
}

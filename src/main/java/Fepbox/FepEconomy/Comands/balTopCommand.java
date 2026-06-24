package Fepbox.FepEconomy.Comands;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.MenuManager.menus.balTop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class balTopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            new balTop(FepEconomy.getDataManger(p), 1).open();
        }
        return true;
    }
}

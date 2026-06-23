package Fepbox.FepEconomy.Comands;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class togglePayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            if (!p.getPersistentDataContainer().has(FepEconomy.getKey(), PersistentDataType.BOOLEAN)) {
                p.getPersistentDataContainer().set(FepEconomy.getKey(), PersistentDataType.BOOLEAN, true);
            }
            if (p.getPersistentDataContainer().get(
                    FepEconomy.getKey(),
                    PersistentDataType.BOOLEAN
            )) {
                p.getPersistentDataContainer().set(FepEconomy.getKey(), PersistentDataType.BOOLEAN, false);
                String msg = FepEconomy.getMessagesCfg().getString("turned-off",
                        "&aSuccessfully &cturned off &apayments");
                p.sendMessage(ColorUtils.translateColorCodes(msg));
            } else {
                p.getPersistentDataContainer().set(FepEconomy.getKey(), PersistentDataType.BOOLEAN, true);
                String msg = FepEconomy.getMessagesCfg().getString("turned-on",
                        "&aSuccessfully turned on payments");
                p.sendMessage(ColorUtils.translateColorCodes(msg));
            }
        }

        return true;
    }
}

package Fepbox.FepEconomy.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.VaultEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;

public class onRightClickEvent implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item == null || item.getItemMeta() == null) return;

        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (item.getItemMeta().getPersistentDataContainer().has(FepEconomy.getKey(), PersistentDataType.DOUBLE)) {
            double amount = item.getItemMeta().getPersistentDataContainer()
                               .get(FepEconomy.getKey(), PersistentDataType.DOUBLE);

            VaultEconomy econ = FepEconomy.getPlugin().getVaultEconomy();

            econ.depositPlayer(e.getPlayer(), amount);

            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                e.getPlayer().getInventory().remove(item);
            }

            e.getPlayer().sendMessage(
                ColorUtils.deserialize(FepEconomy.getMessagesCfg().getString("receive-message").replace("%amount%", econ.format(amount)))
            );

            e.setCancelled(true);
        }
    }
}

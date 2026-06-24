package Fepbox.FepEconomy.MenuManager.listener;

import Fepbox.FepEconomy.MenuManager.MenuManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class ClickHandler implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();

        if (holder instanceof MenuManager) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }

            MenuManager menu = (MenuManager) holder;
            menu.handleMenu(e);
        }
    }
}

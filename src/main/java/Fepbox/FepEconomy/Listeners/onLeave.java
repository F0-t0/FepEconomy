package Fepbox.FepEconomy.Listeners;

import java.sql.SQLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.VaultEconomy;
import Fepbox.FepEconomy.Utils.SQLHelper;
import Fepbox.FepEconomy.Utils.Scheduler;

public class onLeave implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        VaultEconomy econ = FepEconomy.getPlugin().getVaultEconomy();
        econ.getDirty().remove(e.getPlayer().getUniqueId());
        FepEconomy.removeDataManger(e.getPlayer().getUniqueId());
        Scheduler.runAsync(() -> {
            SQLHelper helper = new SQLHelper();
            try {
                helper.savePlayer(e.getPlayer());
                econ.removeFromHashMap(e.getPlayer());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}

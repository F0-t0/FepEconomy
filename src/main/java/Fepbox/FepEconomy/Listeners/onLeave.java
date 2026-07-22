package Fepbox.FepEconomy.Listeners;

import java.sql.SQLException;

import org.bukkit.entity.Player;
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
        Player p = e.getPlayer();
        Scheduler.runAsync(() -> {
            SQLHelper helper = new SQLHelper();
            try {
                helper.savePlayer(p);
                econ.removeFromHashMap(p);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}

package Fepbox.FepEconomy.Listeners;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.SQLHelper;
import Fepbox.FepEconomy.VaultEconomy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class onLeave implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        VaultEconomy econ = FepEconomy.getPlugin().getVaultEconomy();
        econ.getDirty().remove(e.getPlayer().getUniqueId());
        FepEconomy.removeDataManger(e.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(FepEconomy.getPlugin(), () -> {
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

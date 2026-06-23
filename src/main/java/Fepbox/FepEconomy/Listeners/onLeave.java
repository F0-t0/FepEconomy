package Fepbox.FepEconomy.Listeners;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.SQLHelper;
import Fepbox.FepEconomy.VaultEconomy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class onLeave implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        SQLHelper helper = new SQLHelper();
        try {
            helper.savePlayer(e.getPlayer());
            VaultEconomy econ = FepEconomy.getPlugin().getVaultEconomy();
            econ.removeFromHashMap(e.getPlayer());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}

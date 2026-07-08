package Fepbox.FepEconomy.Listeners;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.SQLHelper;
import Fepbox.FepEconomy.VaultEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class onJoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(FepEconomy.getPlugin(), () -> {
            VaultEconomy economy = FepEconomy.getPlugin().getVaultEconomy();
            economy.createPlayerAccount((OfflinePlayer) e.getPlayer());

            SQLHelper sql = FepEconomy.getPlugin().getSQLHelper();
            try {
                sql.updatePlayerName(e.getPlayer().getUniqueId(), e.getPlayer().getName());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            boolean exempt = e.getPlayer().hasPermission("FepEconomy.baltop.exempt") || e.getPlayer().isOp();
            sql.updateExemptStatus(e.getPlayer().getUniqueId(), exempt);
        });
    }
}

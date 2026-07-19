package Fepbox.FepEconomy.Listeners;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.VaultEconomy;
import Fepbox.FepEconomy.Utils.SQLHelper;
import Fepbox.FepEconomy.Utils.Scheduler;

public class onJoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        VaultEconomy economy = FepEconomy.getPlugin().getVaultEconomy();
        economy.createPlayerAccount((OfflinePlayer) e.getPlayer());

        String name = e.getPlayer().getName();
        UUID uuid = e.getPlayer().getUniqueId();
        boolean exempt = e.getPlayer().hasPermission("FepEconomy.baltop.exempt") || e.getPlayer().isOp();

        Scheduler.runAsync(() -> {
            SQLHelper sql = FepEconomy.getPlugin().getSQLHelper();
            try {
                sql.updatePlayerName(uuid, name);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            sql.updateExemptStatus(uuid, exempt);
        });
    }
}

package Fepbox.FepEconomy.Listeners;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.VaultEconomy;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onJoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        VaultEconomy economy = FepEconomy.getPlugin().getVaultEconomy();
        economy.createPlayerAccount((OfflinePlayer) e.getPlayer());
    }
}

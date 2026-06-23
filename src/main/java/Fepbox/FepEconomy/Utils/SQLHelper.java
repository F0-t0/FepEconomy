package Fepbox.FepEconomy.Utils;

import Fepbox.FepEconomy.FepEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class SQLHelper {
    private static Economy econ;

    public SQLHelper() {
        econ = FepEconomy.getPlugin().getVaultEconomy();
    }

    public void savePlayer(OfflinePlayer player) throws SQLException {
        double bal = econ.getBalance(player);
        Connection con = FepEconomy.getPlugin().getConnection();
        try (PreparedStatement ps = con.prepareStatement("INSERT OR REPLACE INTO accounts (uuid, balance) VALUES (?, ?)")) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setDouble(2, bal);
            ps.executeUpdate();
        }
    }

    public void createPlayer(OfflinePlayer player, double bal) throws SQLException {
        Connection con = FepEconomy.getPlugin().getConnection();
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO accounts VALUES (?, ?)")) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setDouble(2, bal);
            ps.executeUpdate();
        }
    }
}

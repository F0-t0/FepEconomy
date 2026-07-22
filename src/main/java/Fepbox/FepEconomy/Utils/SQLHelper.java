package Fepbox.FepEconomy.Utils;

import Fepbox.FepEconomy.FepEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLHelper {
    private static Economy econ;

    public SQLHelper() {
        econ = FepEconomy.getPlugin().getVaultEconomy();
    }

    public void savePlayer(OfflinePlayer player) throws SQLException {
        double bal = econ.getBalance(player);
        String name = player.getName();
        Database.run(con -> {
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE accounts SET name = ?, balance = ? WHERE uuid = ?")) {
                ps.setString(1, name);
                ps.setDouble(2, bal);
                ps.setString(3, player.getUniqueId().toString());
                ps.executeUpdate();
            }
        });
    }

    public void updateExemptStatus(UUID uuid, boolean exempt) {
        try {
            Database.run(con -> {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE accounts SET exempt = ? WHERE uuid = ?")) {
                    ps.setInt(1, exempt ? 1 : 0);
                    ps.setString(2, uuid.toString());
                    ps.executeUpdate();
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createPlayer(OfflinePlayer player, double bal) throws SQLException {
        String name = player.getName();
        Database.run(con -> {
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO accounts (uuid, name, balance) VALUES (?, ?, ?)")) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, name);
                ps.setDouble(3, bal);
                ps.executeUpdate();
            }
        });
    }

    public void updatePlayerName(UUID uuid, String name) throws SQLException {
        Database.run(con -> {
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE accounts SET name = ? WHERE uuid = ?")) {
                ps.setString(1, name);
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            }
        });
    }

    public UUID getUUIDByName(String name) {
        try {
            return Database.call(con -> {
                if (con == null) {
                    return null;
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT uuid FROM accounts WHERE name = ? COLLATE NOCASE LIMIT 1")) {
                    ps.setString(1, name);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return UUID.fromString(rs.getString("uuid"));
                        }
                    }
                }
                return null;
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNamebyUUID(UUID uuid) {
        try {
            return Database.call(con -> {
                if (con == null) {
                    return null;
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT name FROM accounts WHERE uuid = ? COLLATE NOCASE LIMIT 1")) {
                    ps.setString(1, uuid.toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("name");
                        }
                    }
                }
                return null;
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveTransaction(UUID playerUUID, double amount, String sender, String receiver, String status,
            long timestamp) {
        try {
            Database.run(con -> {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO history (player_uuid, amount, sender, receiver, status, timestamp)"
                                + "VALUES (?, ?, ?, ?, ?, ?)"
                )) {
                    ps.setString(1, playerUUID.toString());
                    ps.setDouble(2, amount);
                    ps.setString(3, sender);
                    ps.setString(4, receiver);
                    ps.setString(5, status);
                    ps.setLong(6, timestamp);
                    ps.executeUpdate();
                }
                trimHistory(con, playerUUID, FepEconomy.getPlugin().getConfig().getInt("max-history"));
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void trimHistory(Connection con, UUID playerUUID, int max) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM history "
                        + "WHERE player_uuid = ? "
                        + "AND id NOT IN ( "
                        + "SELECT id FROM history "
                        + "WHERE player_uuid = ? "
                        + "ORDER BY timestamp DESC "
                        + "LIMIT ? "
                        + ")")
        ) {
            ps.setString(1, playerUUID.toString());
            ps.setString(2, playerUUID.toString());
            ps.setInt(3, max);
            ps.executeUpdate();
        }
    }

    public List<UUID> getAllAccounts() throws SQLException {
        return Database.call(con -> {
            List<UUID> uuids = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement("SELECT uuid FROM accounts");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    uuids.add(UUID.fromString(rs.getString("uuid")));
                }
            }
            return uuids;
        });
    }

    public List<Transaction> getHistory(UUID playerUUID, int limit, int page, int pagesize) {
        int offset = (page - 1) * pagesize;
        try {
            return Database.call(con -> {
                List<Transaction> transactions = new ArrayList<>();
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT amount, sender, receiver, status, timestamp FROM history WHERE player_uuid = ? ORDER BY timestamp DESC LIMIT ? OFFSET ?"
                )) {
                    ps.setString(1, playerUUID.toString());
                    ps.setInt(2, limit);
                    ps.setInt(3, offset);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            transactions.add(new Transaction(
                                    rs.getDouble("amount"),
                                    rs.getString("sender"),
                                    rs.getString("receiver"),
                                    rs.getString("status"),
                                    rs.getLong("timestamp")
                            ));
                        }
                    }
                }
                return transactions;
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UUID> getTopPlayers(int offset, int limit) {
        try {
            return Database.call(con -> {
                List<UUID> uuids = new ArrayList<>();
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT uuid FROM accounts WHERE exempt = 0 ORDER BY balance DESC LIMIT ? OFFSET ?"
                )) {
                    ps.setInt(1, limit);
                    ps.setInt(2, offset);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            uuids.add(UUID.fromString(rs.getString("uuid")));
                        }
                    }
                }
                return uuids;
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

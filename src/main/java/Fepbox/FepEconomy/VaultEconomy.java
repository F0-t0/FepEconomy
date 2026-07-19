package Fepbox.FepEconomy;

import Fepbox.FepEconomy.Utils.ColorUtils;
import Fepbox.FepEconomy.Utils.Database;
import Fepbox.FepEconomy.Utils.SQLHelper;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class VaultEconomy implements Economy {

    private final Map<UUID, Double> balances = new ConcurrentHashMap<>();
    private final Set<UUID> dirty = ConcurrentHashMap.newKeySet();
    private FileConfiguration messagesCfg = FepEconomy.getMessagesCfg();
    private String cacheSingular;
    private String cachePlural;
    private String cacheSymbol;
    private boolean cacheUseSymbol;

    private String thousandSuffix;
    private String millionSuffix;
    private String billionSuffix;
    private String trillionSuffix;
    private String quadrillionSuffix;
    private String quintillionSuffix;

    public Set<UUID> getDirty() {
        return dirty;
    }

    public Set<UUID> drainDirty() {
        Set<UUID> snapshot = ConcurrentHashMap.newKeySet();
        snapshot.addAll(dirty);
        dirty.removeAll(snapshot);
        return snapshot;
    }

    public void loadCache() {
        FileConfiguration config = FepEconomy.getPlugin().getConfig();
        this.cacheSingular = config.getString("currency-name-singular", "dollar");
        this.cachePlural = config.getString("currency-name-plural", "dollars");
        this.cacheSymbol = config.getString("symbol", "$");
        this.cacheUseSymbol = config.getBoolean("use-symbol");
        File oldCache = new File(FepEconomy.getPlugin().getDataFolder(), "cache.yml");
        if (oldCache.exists()) {
            oldCache.delete();
        }
        this.thousandSuffix = config.getString("formatting.thousand", "k");
        this.millionSuffix = config.getString("formatting.million", "M");
        this.billionSuffix = config.getString("formatting.billion", "B");
        this.trillionSuffix = config.getString("formatting.trillion", "T");
        this.quadrillionSuffix = config.getString("formatting.quadrillion", "Q");
        this.quintillionSuffix = config.getString("formatting.quintillion", "Qi");
    }

    public void removeFromHashMap(OfflinePlayer player) {
        balances.remove(player.getUniqueId());
    }

    // ============================================================
    // The needed ones
    // ============================================================

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "FepEconomy";
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        String suffix = "";
        if (amount >= 1e18) {
            amount /= 1e18;
            suffix = quintillionSuffix;
        } else if (amount >= 1e15) {
            amount /= 1e15;
            suffix = quadrillionSuffix;
        } else if (amount >= 1e12) {
            amount /= 1e12;
            suffix = trillionSuffix;
        } else if (amount >= 1e9) {
            amount /= 1e9;
            suffix = billionSuffix;
        } else if (amount >= 1e6) {
            amount /= 1e6;
            suffix = millionSuffix;
        } else if (amount >= 1e3) {
            amount /= 1e3;
            suffix = thousandSuffix;
        }
        if (cacheUseSymbol) {
            return String.format("%s%.1f%s", cacheSymbol, amount, suffix);
        }
        return String.format("%.1f%s %s", amount, suffix, amount == 1 ? currencyNameSingular() : currencyNamePlural());
    }

    @Override
    public String currencyNamePlural() {
        return cachePlural;
    }

    @Override
    public String currencyNameSingular() {
        return cacheSingular;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        if (balances.containsKey(uuid)) {
            return true;
        }
        try {
            Double loaded = Database.call(con -> {
                if (con == null) {
                    return null;
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT balance FROM accounts WHERE uuid = ?")) {
                    ps.setString(1, uuid.toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getDouble("balance");
                        }
                    }
                }
                return null;
            });
            if (loaded != null) {
                balances.putIfAbsent(uuid, loaded);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return balances.containsKey(uuid);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        if (!balances.containsKey(player.getUniqueId())) {
            hasAccount(player);
        }
        return balances.getOrDefault(player.getUniqueId(), 0.0);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    ColorUtils
                            .toLegacy(messagesCfg.getString("no-balance", "<red>You cannot withdraw negative amount")));
        }

        UUID uuid = player.getUniqueId();
        hasAccount(player);

        AtomicReference<EconomyResponse> response = new AtomicReference<>();
        balances.compute(uuid, (k, bal) -> {
            if (bal == null) {
                response.set(new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                        ColorUtils.toLegacy(messagesCfg.getString("insufficient-funds",
                                "<red>Insufficient funds"))));
                return null;
            }
            if (bal < amount) {
                response.set(new EconomyResponse(0, bal, EconomyResponse.ResponseType.FAILURE,
                        ColorUtils.toLegacy(messagesCfg.getString("insufficient-funds",
                                "<red>Insufficient funds"))));
                return bal;
            }
            double newBalance = bal - amount;
            dirty.add(k);
            response.set(new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null));
            return newBalance;
        });
        return response.get();
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    ColorUtils.toLegacy(messagesCfg.getString("deposit-negative",
                            "<red>Cannot deposit negative amount")));
        }

        UUID uuid = player.getUniqueId();
        hasAccount(player);

        AtomicReference<EconomyResponse> response = new AtomicReference<>();
        balances.compute(uuid, (k, bal) -> {
            double current = bal == null ? 0.0 : bal;
            double newBalance = current + amount;
            dirty.add(k);
            response.set(new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null));
            return newBalance;
        });
        return response.get();
    }

    public void setBalance(OfflinePlayer player, double amount) {
        UUID uuid = player.getUniqueId();
        balances.put(uuid, amount);
        dirty.add(uuid);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if (hasAccount(player)) {
            return false;
        }
        double startAmount = FepEconomy.getPlugin().getConfig().getDouble("start-amount", 50.0);
        Double existing = balances.putIfAbsent(player.getUniqueId(), startAmount);
        if (existing != null) {
            return false;
        }
        SQLHelper helper = new SQLHelper();
        try {
            helper.createPlayer(player, startAmount);
        } catch (SQLException e) {
            balances.remove(player.getUniqueId(), startAmount);
            throw new RuntimeException(e);
        }
        return true;
    }

    // ============================================================
    // not needed but have to be here
    // ============================================================

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    @Deprecated
    public boolean hasAccount(String playerName) {
        return false;
    }

    @Override
    @Deprecated
    public boolean hasAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    @Deprecated
    public double getBalance(String playerName) {
        return 0.0;
    }

    @Override
    @Deprecated
    public double getBalance(String playerName, String world) {
        return 0.0;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    @Deprecated
    public boolean has(String playerName, double amount) {
        return false;
    }

    @Override
    @Deprecated
    public boolean has(String playerName, String worldName, double amount) {
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not supported");
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not supported");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not supported");
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not supported");
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    // ============================================================
    // Not supported
    // ============================================================

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Banks not supported");
    }

    @Override
    @Deprecated
    public EconomyResponse createBank(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Banks not supported");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Banks not supported");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Banks not supported");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Banks not supported");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Banks not supported");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Banks not supported");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Banks not supported");
    }

    @Override
    @Deprecated
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Banks not supported");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Banks not supported");
    }

    @Override
    @Deprecated
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Banks not supported");
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }
}

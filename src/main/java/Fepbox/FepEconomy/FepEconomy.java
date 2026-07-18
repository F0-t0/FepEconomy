package Fepbox.FepEconomy;

import static java.lang.Double.NaN;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.filter.RegexFilter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Fepbox.FepEconomy.Comands.CommandManager;
import Fepbox.FepEconomy.Comands.balCommand;
import Fepbox.FepEconomy.Comands.balTopCommand;
import Fepbox.FepEconomy.Comands.payCommand;
import Fepbox.FepEconomy.Comands.payHistoryCommand;
import Fepbox.FepEconomy.Comands.togglePayCommand;
import Fepbox.FepEconomy.Listeners.onJoinEvent;
import Fepbox.FepEconomy.Listeners.onLeave;
import Fepbox.FepEconomy.MenuManager.DataManger;
import Fepbox.FepEconomy.MenuManager.listener.ClickHandler;
import Fepbox.FepEconomy.Utils.SQLHelper;
import Fepbox.FepEconomy.Utils.Scheduler;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.milkbowl.vault.economy.Economy;

public final class FepEconomy extends JavaPlugin {

    private static final HashMap<UUID, DataManger> DataManagerMap = new HashMap<>();
    private static FileConfiguration messagesCfg;
    private static FepEconomy plugin;
    private static NamespacedKey key;
    private Connection connection;
    private String dbUrl;
    private VaultEconomy vaultEconomy;
    private SQLHelper sql;
    private ScheduledTask saveTask;
    private final String user = "F0-t0";
    private final String repo = "FepEconomy";
    private boolean isnewVersion;

    public static FepEconomy getPlugin() {
        return plugin;
    }

    public boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static OfflinePlayer getOfflinePlayerByName(String name) {
        VaultEconomy econ = getPlugin().getVaultEconomy();
        SQLHelper sqlHelper = getPlugin().getSQLHelper();

        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (econ.hasAccount(player)) {
            return player;
        }

        UUID uuid = sqlHelper.getUUIDByName(name);
        if (uuid != null) {
            return Bukkit.getOfflinePlayer(uuid);
        }

        return player;
    }

    public static DataManger getDataManger(Player p) {
        return DataManagerMap.computeIfAbsent(p.getUniqueId(), k -> new DataManger(p));
    }

    public static void removeDataManger(UUID uuid) {
        DataManagerMap.remove(uuid);
    }

    public static FileConfiguration getMessagesCfg() {
        return messagesCfg;
    }

    public static double parseAmount(String input) {
        if (input == null || input.isEmpty()) {
            return -1;
        }
        input = input.trim().replace(',', '.');
        FileConfiguration config = getPlugin().getConfig();

        String[] suffixes = {
                config.getString("formatting.quintillion", "Qi"),
                config.getString("formatting.quadrillion", "Q"),
                config.getString("formatting.trillion", "T"),
                config.getString("formatting.billion", "B"),
                config.getString("formatting.million", "M"),
                config.getString("formatting.thousand", "k")
        };
        double[] multipliers = { 1e18, 1e15, 1e12, 1e9, 1e6, 1e3 };

        double multiplier = 1;
        for (int i = 0; i < suffixes.length; i++) {
            String suffix = suffixes[i];
            if (suffix == null || suffix.isEmpty() || suffix.length() > input.length()) {
                continue;
            }
            if (input.regionMatches(true, input.length() - suffix.length(), suffix, 0, suffix.length())) {
                multiplier = multipliers[i];
                input = input.substring(0, input.length() - suffix.length()).trim();
                break;
            }
        }

        try {
            return Double.parseDouble(input) * multiplier;
        } catch (NumberFormatException e) {
            return NaN;
        }
    }

    public static NamespacedKey getKey() {
        return key;
    }

    @Override
    public void onEnable() {
        plugin = this;

        int pluginId = 32204;
        Metrics metrics = new Metrics(plugin, pluginId);

        key = new NamespacedKey(plugin, "togglePay");
        saveDefaultConfig();
        File file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) {
            saveResource("messages.yml", false);
        }
        messagesCfg = YamlConfiguration.loadConfiguration(file);
        try {
            Class.forName("org.sqlite.JDBC");
            this.dbUrl = "jdbc:sqlite:" + getDataFolder() + "/data.db";
            this.connection = DriverManager.getConnection(dbUrl);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS accounts (uuid TEXT PRIMARY KEY, name TEXT, balance DOUBLE DEFAULT 0, exempt INTEGER DEFAULT 0)");
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS history (id INTEGER PRIMARY KEY AUTOINCREMENT, player_uuid TEXT NOT NULL, amount DOUBLE NOT NULL, sender TEXT NOT NULL, receiver TEXT NOT NULL, status TEXT NOT NULL, timestamp BIGINT NOT NULL)");
            }
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("ALTER TABLE accounts ADD COLUMN exempt INTEGER DEFAULT 0");
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.vaultEconomy = new VaultEconomy();
        this.vaultEconomy.loadCache();
        getServer().getServicesManager().register(Economy.class, this.vaultEconomy, this, ServicePriority.Normal);
        getServer().getPluginManager().registerEvents(new ClickHandler(), this);
        getServer().getPluginManager().registerEvents(new onLeave(), this);
        getServer().getPluginManager().registerEvents(new onJoinEvent(), this);
        getCommand("bal").setExecutor(new balCommand());

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            CommandManager.register(plugin, commands.registrar());
        });

        getCommand("pay").setExecutor(new payCommand());
        getCommand("togglepay").setExecutor(new togglePayCommand());
        getCommand("payhistory").setExecutor(new payHistoryCommand());
        getCommand("baltop").setExecutor(new balTopCommand());

        sql = new SQLHelper();

        for (Player p : Bukkit.getOnlinePlayers()) {
            vaultEconomy.createPlayerAccount(p);
        }

        if (getConfig().getBoolean("suppress-warnings")) {
            filterErrors();
        }

        startSaveTask();
        Scheduler.runLater(
                () -> {
                    Bukkit.getConsoleSender().sendMessage("""
                             §6 ______         ______                                     \s
                             §6|  ____|       |  ____|                                    \s
                             §6| |__ ___ _ __ | |__   ___ ___  _ __   ___  _ __ ___  _   _\s
                             §6|  __/ _ \\ '_ \\|  __| / __/ _ \\| '_ \\ / _ \\| '_ ` _ \\| | | |
                             §6| | |  __/ |_) | |___| (_| (_) | | | | (_) | | | | | | |_| |
                             §6|_|  \\___| .__/|______\\___\\___/|_| |_|\\___/|_| |_| |_|\\__, |
                             §6         | |                                           __/ |
                             §6         |_|                                          |___/\s
                            """);
                    String newVer = checkUpdates("2.3");
                    String newUpdate = isnewVersion ? "§4New Version avaible: " + newVer : "§7You're up to date!";
                    Bukkit.getConsoleSender().sendMessage("""

                            §dAutor: §7Foto
                            §dVersion: 2.3

                            §dUpdate:
                            """
                            + newUpdate);
                }, 300L);

    }

    private String checkUpdates(String ver) {
        try {
            URL url = new URL(
                    "https://api.github.com/repos/" + user + "/" + repo + "/releases/latest");

            JsonObject json = JsonParser
                    .parseReader(new InputStreamReader(url.openStream()))
                    .getAsJsonObject();

            String latestVersion = json.get("tag_name").getAsString();

            if (!ver.equalsIgnoreCase(latestVersion)) {
                isnewVersion = true;
            } else {
                isnewVersion = false;
            }
            return latestVersion;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ver;
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                sql.savePlayer(player);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(1)) {
                if (connection != null && !connection.isClosed()) {
                    try {
                        connection.close();
                    } catch (SQLException ignored) {
                    }
                }
                connection = DriverManager.getConnection(dbUrl);
            }
        } catch (SQLException e) {
            getLogger().severe("Failed to (re)connect to DB: " + e.getMessage());
        }
        return connection;
    }

    public VaultEconomy getVaultEconomy() {
        return vaultEconomy;
    }

    public SQLHelper getSQLHelper() {
        return sql;
    }

    private void startSaveTask() {
        this.saveTask = Scheduler.runAsyncTimer(() -> {
            Set<UUID> dirty = vaultEconomy.getDirty();
            if (dirty.isEmpty()) {
                return;
            }
            Set<UUID> snapshot = new HashSet<>(dirty);
            dirty.clear();
            for (UUID uuid : snapshot) {
                try {
                    sql.savePlayer(Bukkit.getOfflinePlayer(uuid));
                } catch (SQLException e) {
                    getLogger().warning("Failed to save " + uuid + ": " + e.getMessage());
                }
            }
        }, 500L, getConfig().getLong("autosave", 30L) * 20L);
    }

    public void reloadMessages() {
        File file = new File(getDataFolder(), "messages.yml");
        messagesCfg = YamlConfiguration.loadConfiguration(file);
    }

    public void restartSaveTask() {
        if (saveTask != null) {
            saveTask.cancel();
        }
        startSaveTask();
    }

    private void filterErrors() {
        try {
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            Configuration config = ctx.getConfiguration();

            RegexFilter filter = RegexFilter.createFilter(
                    ".*Couldn't look up profile properties.*",
                    null,
                    false,
                    RegexFilter.Result.DENY,
                    RegexFilter.Result.NEUTRAL);
            filter.start();
            config.getRootLogger().addFilter(filter);
            ctx.updateLoggers();

        } catch (Exception e) {
            getLogger().warning("Nie udało się zaaplikować filtra logów: " + e.getMessage());
        }
    }
}

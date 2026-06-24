package Fepbox.FepEconomy.CommandManager.commands.subcommands;

import Fepbox.FepEconomy.CommandManager.SubCommand;
import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import Fepbox.FepEconomy.Utils.SQLHelper;
import Fepbox.FepEconomy.VaultEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class reset extends SubCommand {
    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "Reset player balance to start amount";
    }

    @Override
    public String getSyntax() {
        return "/eco reset <player|all>";
    }

    @Override
    public void preform(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorUtils.translateColorCodes(
                    FepEconomy.getMessagesCfg().getString("args", "&cNot enough arguments")));
            return;
        }

        VaultEconomy econ = FepEconomy.getPlugin().getVaultEconomy();
        double startAmount = FepEconomy.getPlugin().getConfig().getDouble("start-amount", 50.0);
        FileConfiguration msg = FepEconomy.getMessagesCfg();

        if (args[1].equalsIgnoreCase("all")) {
            SQLHelper sql = new SQLHelper();
            try {
                List<UUID> uuids = sql.getAllAccounts();
                for (UUID uuid : uuids) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
                    if (econ.hasAccount(target)) {
                        econ.setBalance(target, startAmount);
                    }
                }
                String response = msg.getString("reset-all", "&aReset %count% players to %amount%");
                response = response.replace("%count%", String.valueOf(uuids.size()));
                response = response.replace("%amount%", econ.format(startAmount));
                sender.sendMessage(ColorUtils.translateColorCodes(response));
            } catch (SQLException e) {
                sender.sendMessage(ColorUtils.translateColorCodes("&cDatabase error: " + e.getMessage()));
            }
        } else {
            OfflinePlayer target = FepEconomy.getOfflinePlayerByName(args[1]);
            if (!econ.hasAccount(target)) {
                sender.sendMessage(ColorUtils.translateColorCodes(
                        msg.getString("player-not-found", "&cCould not find player %player%")
                                .replace("%player%", args[1])));
                return;
            }
            econ.setBalance(target, startAmount);

            String response = msg.getString("reset", "&aReset %player%'s balance to %amount%");
            response = response.replace("%player%", args[1]);
            response = response.replace("%amount%", econ.format(startAmount));
            sender.sendMessage(ColorUtils.translateColorCodes(response));
        }
    }
}

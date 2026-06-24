package Fepbox.FepEconomy.Comands;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import Fepbox.FepEconomy.Utils.SQLHelper;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class payCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorUtils.translateColorCodes(FepEconomy.getMessagesCfg().getString("args",
                    "&cNot enough arguments")));
            return true;
        }

        if (Bukkit.getPlayer(args[0]) == null) {
            String nf = FepEconomy.getMessagesCfg().getString("player-not-found",
                    "&cCould not find player %player%");
            nf = nf.replace("%player%", args[0]);
            sender.sendMessage(ColorUtils.translateColorCodes(nf));

            return true;
        };

        if (Bukkit.getPlayer(args[0]) == (Player) sender) {
            String cy = FepEconomy.getMessagesCfg().getString("same-player");
            sender.sendMessage(ColorUtils.translateColorCodes(cy));
            return true;
        }
        if (!Bukkit.getPlayer(args[0]).getPersistentDataContainer().has(FepEconomy.getKey(), PersistentDataType.BOOLEAN)) {
            Bukkit.getPlayer(args[0]).getPersistentDataContainer().set(FepEconomy.getKey(), PersistentDataType.BOOLEAN, true);
        }
        if (!Bukkit.getPlayer(args[0]).getPersistentDataContainer().get(FepEconomy.getKey(), PersistentDataType.BOOLEAN)) {
            String to = FepEconomy.getMessagesCfg().getString("player-turned-off-payments", "&c%receiver% has turned off payments");
            to.replace("%receiver%", args[0]);
            sender.sendMessage(ColorUtils.translateColorCodes(to));

            return true;
        }

        double amount = FepEconomy.parseAmount(args[1]);

        Economy econ = FepEconomy.getPlugin().getVaultEconomy();

        if (amount > econ.getBalance((OfflinePlayer) sender)) {
            sender.sendMessage(ColorUtils.translateColorCodes(FepEconomy.getMessagesCfg().getString("insufficient-funds", "&cInsufficient funds")));
            return true;
        }

        if (amount == -1) {
            sender.sendMessage(ColorUtils.translateColorCodes(
                    FepEconomy.getMessagesCfg().getString("invalid-number", "&cInvalid number format")
            ));
            return true;
        }


        econ.withdrawPlayer((OfflinePlayer) sender, amount);
        econ.depositPlayer((OfflinePlayer) Bukkit.getPlayer(args[0]), amount);

        String smsg = FepEconomy.getMessagesCfg().getString("sent");
        smsg = smsg.replace("%amount%", econ.format(amount));
        smsg = smsg.replace("%receiver%", args[0]);
        sender.sendMessage(ColorUtils.translateColorCodes(smsg));

        String rmsg = FepEconomy.getMessagesCfg().getString("received");
        rmsg = rmsg.replace("%amount%", econ.format(amount));
        rmsg = rmsg.replace("%sender%", sender.getName());
        Bukkit.getPlayer(args[0]).sendMessage(ColorUtils.translateColorCodes(rmsg));

        SQLHelper sql = new SQLHelper();
        Player target = (Player) Bukkit.getPlayer(args[0]);
        Player p = (Player) sender;

        String statusR = FepEconomy.getMessagesCfg().getString("status-received");
        String statusS = FepEconomy.getMessagesCfg().getString("status-sent");

        Bukkit.getScheduler().runTaskAsynchronously(FepEconomy.getPlugin(), () -> {
            // The sender save
            sql.saveTransaction(p.getUniqueId(),
                    amount,
                    p.getName(),
                    target.getName(),
                    statusS,
                    System.currentTimeMillis());
            // The receiver save
            sql.saveTransaction(target.getUniqueId(),
                    amount,
                    p.getName(),
                    target.getName(),
                    statusR,
                    System.currentTimeMillis());
        });


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        } else if (args.length == 2) {
            completions.add("1000");
            completions.add("2500");
            completions.add("5K");
            completions.add("1M");
            completions.add("1B");
        }
        return completions;
    }
}

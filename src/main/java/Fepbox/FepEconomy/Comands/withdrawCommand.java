package Fepbox.FepEconomy.Comands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.VaultEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.EconomyResponse;

public class withdrawCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {

        if (sender instanceof Player p) {
            VaultEconomy econ = FepEconomy.getPlugin().getVaultEconomy();

            String amountString = args[0];
            double amount = FepEconomy.parseAmount(amountString);

            if (Double.isNaN(amount)) {
                String msg = FepEconomy.getMessagesCfg().getString("invalid-number");
                p.sendMessage(ColorUtils.deserialize(msg));
                return true;
            }

            if (!econ.has(p, Double.parseDouble(args[0]))) {
                String msg = FepEconomy.getMessagesCfg().getString("insufficient-funds");
                p.sendMessage(ColorUtils.deserialize(msg));
                return true;
            }
            EconomyResponse response = econ.withdrawPlayer((OfflinePlayer) sender, Double.parseDouble(args[0]));
            if (response.transactionSuccess()) {
                FileConfiguration messages = FepEconomy.getMessagesCfg();
                Material m = Material.getMaterial(messages.getString("check.material"));
                ItemStack check = new ItemStack(m);
                ItemMeta metaCheck = check.getItemMeta();
                String displayName = messages.getString("check.displayname");
                metaCheck.displayName(ColorUtils.deserialize(displayName));

                List<String> loreStrings = messages.getStringList("check.lore");
                List<Component> lore = new ArrayList<>();

                for (String loreString : loreStrings) {
                    loreString = loreString.replace("%amount%", econ.format(amount)).replace("%owner%", sender.getName());
                    lore.add(ColorUtils.deserialize(loreString));
                }

                metaCheck.lore(lore);

                metaCheck.getPersistentDataContainer().set(FepEconomy.getKey(), PersistentDataType.DOUBLE, amount);
                check.setItemMeta(metaCheck);
                p.getInventory().addItem(check);

                return true;
            }
        }
        return true;
    }

}

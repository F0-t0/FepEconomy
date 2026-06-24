package Fepbox.FepEconomy.MenuManager.menus;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.MenuManager.DataManger;
import Fepbox.FepEconomy.MenuManager.MenuManager;
import Fepbox.FepEconomy.Utils.ColorUtils;
import Fepbox.FepEconomy.Utils.SQLHelper;
import Fepbox.FepEconomy.Utils.Transaction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PayHistory extends MenuManager {
    private int page = 1;
    private final Player target;

    public PayHistory(DataManger dataManger, int page, Player target) {
        super(dataManger);
        this.page = page;
        this.target = target;
    }

    @Override
    public String getTitle() {
        return ColorUtils.translateColorCodes(FepEconomy.getMessagesCfg().getString("guiTitle", "&aTransaction History"));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void SetItems() {
        ItemStack it = createItem(Material.SPECTRAL_ARROW, ColorUtils.translateColorCodes(
                FepEconomy.getMessagesCfg().getString("nextPage-name", "&6Next Page")
        ));
        ItemStack bk = createItem(Material.TIPPED_ARROW, ColorUtils.translateColorCodes(
                FepEconomy.getMessagesCfg().getString("previousPage-name", "&6Next Page")
        ));
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        List<Transaction> transactions;
        SQLHelper sql = new SQLHelper();
        if (target != null) {
            transactions = sql.getHistory(target.getUniqueId(), 50, page, 45);
        } else {
            transactions = sql.getHistory(dataManger.getOwner().getUniqueId(), 50, page, 45);
        }

        int id = 0;
        for (Transaction transaction : transactions) {
            Economy econ = FepEconomy.getPlugin().getVaultEconomy();
            List<String> lore = FepEconomy.getMessagesCfg().getStringList("Transaction-Lore");
            String time = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(transaction.timestamp()));
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, ColorUtils.translateColorCodes(lore.get(i).replace("%amount%", econ.format(transaction.amount()))));
                lore.set(i, ColorUtils.translateColorCodes(lore.get(i).replace("%sender%", String.valueOf(transaction.sender()))));
                lore.set(i, ColorUtils.translateColorCodes(lore.get(i).replace("%receiver%", String.valueOf(transaction.receiver()))));
                lore.set(i, ColorUtils.translateColorCodes(lore.get(i).replace("%status%", String.valueOf(transaction.status()))));
                lore.set(i, ColorUtils.translateColorCodes(lore.get(i).replace("%time%", time)));
            }
            ItemStack t = new ItemStack(Material.PAPER);
            ItemMeta tMeta = t.getItemMeta();
            String name = FepEconomy.getMessagesCfg().getString("Transaction-name");
            assert name != null;
            name = name.replace("%time%", time);
            name = name.replace("%amount%", econ.format(transaction.amount()));
            name = name.replace("%sender%", String.valueOf(transaction.sender()));
            name = name.replace("%receiver%", String.valueOf(transaction.receiver()));
            name = name.replace("%status%", String.valueOf(transaction.status()));

            tMeta.setDisplayName(ColorUtils.translateColorCodes(name));
            tMeta.setLore(lore);

            t.setItemMeta(tMeta);
            if (id > 44) {
                break;
            }
            inventory.setItem(id, t);
            id++;
        }
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, filler);
        }
        if (page != (FepEconomy.getPlugin().getConfig().getInt("max-history", 100) / 45)) {
            inventory.setItem(50, it);
        }
        if (page != 1) {
            inventory.setItem(48, bk);
        }


    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) {
            return;
        }
        if (e.getCurrentItem().getType() == Material.SPECTRAL_ARROW) {
            if (page == (FepEconomy.getPlugin().getConfig().getInt("max-history", 100) / 45)) {
                return;
            }
            new PayHistory(FepEconomy.getDataManger(dataManger.getOwner()), page + 1, target).open();
        } else if (e.getCurrentItem().getType() == Material.TIPPED_ARROW) {
            if (page == 1) {
                return;
            }
            new PayHistory(FepEconomy.getDataManger(dataManger.getOwner()), page - 1, target).open();
        }
    }
}

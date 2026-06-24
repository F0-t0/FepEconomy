package Fepbox.FepEconomy.Utils;

import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.UUID;

public class HeadCache {
    private static HashMap<UUID, SkullMeta> headCache = new HashMap<>();

    public static void saveHead(UUID uuid, SkullMeta meta) {
        headCache.put(uuid, meta);
    }

    public static SkullMeta getHead(UUID uuid) {
        return headCache.get(uuid);
    }

    public static void clearHead(UUID uuid) {
        headCache.remove(uuid);
    }
}

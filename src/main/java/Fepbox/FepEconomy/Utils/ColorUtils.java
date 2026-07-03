package Fepbox.FepEconomy.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ColorUtils {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component deserialize(String text) {
        return miniMessage.deserialize(text);
    }

    public static String toLegacy(String miniMessageText) {
        return LegacyComponentSerializer.legacySection().serialize(
                miniMessage.deserialize(miniMessageText)
        );
    }

}

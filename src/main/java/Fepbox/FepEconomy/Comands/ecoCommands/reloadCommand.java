package Fepbox.FepEconomy.Comands.ecoCommands;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class reloadCommand {
    private static FepEconomy plugin;

    public reloadCommand(FepEconomy plugin) {
        this.plugin = plugin;
    }

    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("reload")
                .requires(sender -> sender.getSender().hasPermission("fepeconomy.admin"))
                .executes(ctx -> {
                    FepEconomy.getPlugin().reloadConfig();
                    FepEconomy.getPlugin().reloadMessages();
                    FepEconomy.getPlugin().getVaultEconomy().loadCache();
                    FepEconomy.getPlugin().restartSaveTask();

                    ctx.getSource().getSender().sendMessage(ColorUtils.deserialize(
                            FepEconomy.getMessagesCfg().getString("reload", "<green>Configuration reloaded successfully")));

                    return Command.SINGLE_SUCCESS;
                });
    }
}

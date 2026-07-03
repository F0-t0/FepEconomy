package Fepbox.FepEconomy.Comands.ecoCommands;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import Fepbox.FepEconomy.VaultEconomy;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Collection;

public class takeCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("take")
                .requires(sender -> sender.getSender().hasPermission("fepeconomy.admin"))
                .then(Commands.argument("player", ArgumentTypes.playerProfiles())
                        .then(Commands.argument("amount", StringArgumentType.string())
                                .suggests((ctx, builder) -> {
                                    builder.suggest("100");
                                    builder.suggest("1k");
                                    builder.suggest("1m");
                                    builder.suggest("1b");

                                    return builder.buildFuture();
                                })
                                .executes(ctx -> execute(ctx))));
    }


    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        VaultEconomy econ = FepEconomy.getPlugin().getVaultEconomy();
        PlayerProfileListResolver r1 = ctx.getArgument("player", PlayerProfileListResolver.class);
        Collection<PlayerProfile> col = r1.resolve(ctx.getSource());
        for (PlayerProfile profile : col) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(profile.getId());
            if (target == null || !econ.hasAccount(target)) {
                ctx.getSource().getSender().sendMessage(ColorUtils.deserialize(
                        FepEconomy.getMessagesCfg().getString("player-not-found", "<red>Could not find player")));
                return Command.SINGLE_SUCCESS;
            }
            String amountS = ctx.getArgument("amount", String.class);
            double amount = FepEconomy.parseAmount(amountS);
            if (Double.isNaN(amount)) {
                ctx.getSource().getSender().sendMessage(ColorUtils.deserialize(
                        FepEconomy.getMessagesCfg().getString("invalid-number", "<red>Invalid number format")));
                return Command.SINGLE_SUCCESS;
            }
            econ.withdrawPlayer(target, amount);

            Component msg = ColorUtils.deserialize(
                    FepEconomy.getMessagesCfg().getString("take",
                            "<green>Took %amount% from %player%'s balance")
                            .replace("%amount%", econ.format(amount))
                            .replace("%player%", target.getName()));
            ctx.getSource().getSender().sendMessage(msg);
        }


        return Command.SINGLE_SUCCESS;
    }
}

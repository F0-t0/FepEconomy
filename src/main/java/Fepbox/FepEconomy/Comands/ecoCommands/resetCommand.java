package Fepbox.FepEconomy.Comands.ecoCommands;

import Fepbox.FepEconomy.FepEconomy;
import Fepbox.FepEconomy.Utils.ColorUtils;
import Fepbox.FepEconomy.VaultEconomy;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.Command;
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
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;

public class resetCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("reset")
                .requires(sender -> sender.getSender().hasPermission("fepeconomy.admin"))
                .then(Commands.argument("player", ArgumentTypes.playerProfiles())
                        .executes(ctx -> execute(ctx)));
    }


    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        VaultEconomy econ = FepEconomy.getPlugin().getVaultEconomy();
        double startAmount = FepEconomy.getPlugin().getConfig().getDouble("start-amount", 50.0);
        FileConfiguration msg = FepEconomy.getMessagesCfg();

        PlayerProfileListResolver r1 = ctx.getArgument("player", PlayerProfileListResolver.class);
        Collection<PlayerProfile> col = r1.resolve(ctx.getSource());
        for (PlayerProfile profile : col) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(profile.getId());

            if (target == null) {
                ctx.getSource().getSender().sendMessage(ColorUtils.deserialize(
                        FepEconomy.getMessagesCfg().getString("player-not-found", "<red>Could not find the player")));
                return Command.SINGLE_SUCCESS;
            }

            if (!econ.hasAccount(target)) {
                ctx.getSource().getSender().sendMessage(ColorUtils.deserialize(
                        FepEconomy.getMessagesCfg().getString("player-not-found", "<red>Could not find the player")));
                return Command.SINGLE_SUCCESS;
            }

            econ.setBalance(target, startAmount);

            Component response = ColorUtils.deserialize(
                    msg.getString("reset", "<green>Reset %player%'s balance to %amount%")
                            .replace("%player%", target.getName())
                            .replace("%amount%", econ.format(startAmount)));
            ctx.getSource().getSender().sendMessage(response);
        }


        return Command.SINGLE_SUCCESS;
    }
}

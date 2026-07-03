package Fepbox.FepEconomy.Comands;

import Fepbox.FepEconomy.Comands.ecoCommands.*;
import Fepbox.FepEconomy.FepEconomy;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class CommandManager {
    public static void register(FepEconomy plugin, Commands registrar) {
        LiteralCommandNode<CommandSourceStack> root = Commands.literal("eco")
                .then(addCommand.build())
                .then(new reloadCommand(FepEconomy.getPlugin()).build())
                .then(resetCommand.build())
                .then(setCommand.build())
                .then(takeCommand.build())
                .build();
        registrar.register(root);
    }
}

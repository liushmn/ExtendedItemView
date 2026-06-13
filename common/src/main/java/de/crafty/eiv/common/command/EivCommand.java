package de.crafty.eiv.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import de.crafty.eiv.common.recipe.ClientRecipeManager;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class EivCommand {


    private static int reloadRecipes(CommandContext<CommandSourceStack> context){
        ClientRecipeManager.INSTANCE.reload();
        context.getSource().sendSuccess(() -> Component.translatable("commands.eiv.reloadedRecipes"), true);
        return 1;
    }

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(
                Commands.literal("eiv")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(3))
                        .then(Commands.literal("reload").executes(EivCommand::reloadRecipes))
        );
    }

}

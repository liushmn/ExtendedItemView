package de.crafty.eiv.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class EivCommand {


    private static int reloadRecipes(CommandContext<CommandSourceStack> context) {
        ServerRecipeManager.INSTANCE.reloadRecipes();
        ServerRecipeManager.INSTANCE.broadcastAllRecipes();
        context.getSource().sendSuccess(() -> Component.translatable("commands.eiv.reloadedRecipes"), true);
        return 1;
    }

    private static int reloadStackSensitives(CommandContext<CommandSourceStack> context){
        ServerRecipeManager.INSTANCE.informAboutStackSensitives();
        context.getSource().sendSuccess(() -> Component.translatable("commands.eiv.reloadedStackSensitives"), true);
        return 1;
    }

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(
                Commands.literal("eiv")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(3))
                        .then(Commands.literal("reloadRecipes").executes(EivCommand::reloadRecipes))
                        .then(Commands.literal("reloadStackSensitives").executes(EivCommand::reloadStackSensitives))
        );
    }

}

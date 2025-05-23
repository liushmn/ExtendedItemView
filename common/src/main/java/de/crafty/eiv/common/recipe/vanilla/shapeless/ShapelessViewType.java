package de.crafty.eiv.common.recipe.vanilla.shapeless;

import de.crafty.eiv.common.recipe.vanilla.crafting.CraftingViewType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ShapelessViewType extends CraftingViewType {

    public static final ShapelessViewType INSTANCE = new ShapelessViewType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.shapeless");
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.withDefaultNamespace("crafting_shapeless");
    }
}

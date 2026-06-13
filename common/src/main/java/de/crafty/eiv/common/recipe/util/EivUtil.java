package de.crafty.eiv.common.recipe.util;

import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;

public class EivUtil {


    public static boolean matchesAnyTransferClass(IEivViewRecipe viewRecipe, Screen playerScreen) {
        if (playerScreen == null)
            return false;

        return viewRecipe.getTransferClasses().stream().anyMatch(screenClass -> screenClass.isInstance(playerScreen));
    }


    public static void addLoreLine(ItemStack stack, Component component){
        CompoundTag stackTag = stack.getOrCreateTag();
        CompoundTag displayTag = stackTag.contains("display") ? stackTag.getCompound("display") : new CompoundTag();
        ListTag lore = displayTag.contains("Lore") ? displayTag.getList("Lore", 9) : new ListTag();
        lore.add(StringTag.valueOf(Component.Serializer.toJson(component)));
        displayTag.put("Lore", lore);
        stackTag.put("display", displayTag);
    }

    public static ResourceLocation uniqueIdFromItemMix(PotionBrewing.Mix<Item> itemMix) {
        ResourceLocation fromId = BuiltInRegistries.ITEM.getKey(itemMix.from);
        ResourceLocation toId = BuiltInRegistries.ITEM.getKey(itemMix.to);

        return new ResourceLocation(toId.getNamespace(), fromId + "_" + EivUtil.uniqueIdFromIngredient(itemMix.ingredient) + "_" + toId);
    }

    public static ResourceLocation uniqueIdFromPotionMix(PotionBrewing.Mix<Potion> potionMix) {
        ResourceLocation fromId = BuiltInRegistries.POTION.getKey(potionMix.from);
        ResourceLocation toId = BuiltInRegistries.POTION.getKey(potionMix.to);

        return new ResourceLocation(toId.getNamespace(), fromId + "_" + EivUtil.uniqueIdFromIngredient(potionMix.ingredient) + "_" + toId);
    }

    public static String uniqueIdFromIngredient(Ingredient ingredient) {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < ingredient.values.length; i++) {
            Ingredient.Value value = ingredient.values[i];

            if(value instanceof Ingredient.ItemValue itemValue)
                builder.append(itemValue.getItems().iterator().next()).append(i < ingredient.values.length - 1 ? "_" : "");

            if(value instanceof Ingredient.TagValue tagValue)
                builder.append(tagValue.tag.location()).append(i < ingredient.values.length - 1 ? "_" : "");

        }

        return builder.toString();
    }
}

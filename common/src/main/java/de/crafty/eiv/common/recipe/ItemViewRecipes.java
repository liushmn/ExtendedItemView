package de.crafty.eiv.common.recipe;

import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.material.Fluid;

import java.util.*;

/**
 * Internal (intermediate) class that connects {@link de.crafty.eiv.common.api.recipe.ItemView} (Api-class) with EIV logic
 * <br>
 * <br>
 * Also contains some helper functions
 */
public class ItemViewRecipes {

    public static final ItemViewRecipes INSTANCE = new ItemViewRecipes();

    /**
     * A map of recipe providers
     */
    private final List<RecipeProvider> recipeProviders;

    /**
     * A map of items by fluid
     */
    private final HashMap<Fluid, Item> fluidItemMap;

    private ItemViewRecipes() {
        this.recipeProviders = new ArrayList<>();
        this.fluidItemMap = new HashMap<>();
    }


    /**
     * Old way to register recipe providers
     * <br>
     * <br>
     * Will be removed soon
     * @param provider
     */
    @Deprecated
    public void addRecipeProvider(RecipeProvider provider) {
        this.recipeProviders.add(provider);
    }


    public List<RecipeProvider> getRecipeProviders() {
        return this.recipeProviders;
    }

    public void setFluidItemMap(HashMap<Fluid, Item> fluidItemMap) {
        this.fluidItemMap.clear();
        this.fluidItemMap.putAll(fluidItemMap);
    }

    /**
     *
     * @param fluid The fluid
     * @return The corresponding item to a fluid
     */
    public Item itemForFluid(Fluid fluid) {
        return this.fluidItemMap.getOrDefault(fluid, Items.AIR);
    }

    /**
     *
     * @return Whether any of the listed SlotContents contains an itemStack matching the potion of the given stack
     */
    public static boolean makePotionRedirectCheck(ItemStack stack, List<SlotContent> slotContents) {
        if (!stack.hasTag() || !stack.getTag().contains("Potion"))
            return true;

        for (SlotContent slotContent : slotContents) {
            for (ItemStack validStack : slotContent.getValidContents()) {
                if (!stack.is(validStack.getItem()))
                    continue;

                if (ItemViewRecipes.makePotionCheck(stack, validStack))
                    return true;
            }
        }

        return false;
    }

    /**
     *
     * @return Whether any of the listed SlotContents contains an itemStack matching the enchantments of the given stack
     */
    public static boolean makeEnchantedRedirectCheck(ItemStack stack, List<SlotContent> slotContents) {
        if (EnchantmentHelper.getEnchantments(stack).isEmpty())
            return true;

        for (SlotContent slotContent : slotContents) {
            for (ItemStack validStack : slotContent.getValidContents()) {

                if (!stack.is(validStack.getItem()))
                    continue;

                if (ItemViewRecipes.makeEnchantmentCheck(stack, validStack))
                    return true;
            }

        }

        return false;
    }

    /**
     * @return Whether the potion component of two itemStacks matches
     */
    public static boolean makePotionCheck(ItemStack stack1, ItemStack stack2) {
        if (!stack1.hasTag() || !stack2.hasTag())
            return true;

        CompoundTag stackTag1 = stack1.getTag();
        CompoundTag stackTag2 = stack2.getTag();

        if(!stackTag1.contains("Potion") || !stackTag2.contains("Potion"))
            return true;

        //TODO probably compare by effects
        return PotionUtils.getPotion(stackTag1).equals(PotionUtils.getPotion(stackTag2));

    }

    /**
     * @return Whether the enchantments of two itemStacks match
     */
    public static boolean makeEnchantmentCheck(ItemStack stack1, ItemStack stack2) {
        if (EnchantmentHelper.getEnchantments(stack1).isEmpty() || EnchantmentHelper.getEnchantments(stack2).isEmpty())
            return true;

        Map<Enchantment, Integer> stack1Enchantments = EnchantmentHelper.getEnchantments(stack1);
        Map<Enchantment, Integer> stack2Enchantments = EnchantmentHelper.getEnchantments(stack2);

        return stack1Enchantments.keySet().stream().allMatch(
                enchantment ->
                        stack2Enchantments.containsKey(enchantment) && Objects.equals(stack1Enchantments.get(enchantment), stack2Enchantments.get(enchantment))
        ) && stack1Enchantments.size() == stack2Enchantments.size();
    }



    public interface RecipeProvider {

        void provide(List<IEivViewRecipe> recipeList);

    }


}

package de.crafty.eiv.servercompat.builtin;

import de.crafty.eiv.servercompat.api.CompatItemView;
import de.crafty.eiv.servercompat.api.IEivCompatIntegration;
import de.crafty.eiv.servercompat.builtin.blasting.CompatBlastingRecipe;
import de.crafty.eiv.servercompat.builtin.shaped.CompatShapedRecipe;
import de.crafty.eiv.servercompat.builtin.shapeless.CompatShapelessRecipe;
import de.crafty.eiv.servercompat.builtin.smelting.CompatSmeltingRecipe;
import de.crafty.eiv.servercompat.builtin.smoking.CompatSmokingRecipe;
import de.crafty.eiv.servercompat.builtin.stonecutting.CompatStonecutterRecipe;
import de.crafty.eiv.servercompat.recipe.CompatRecipeManager;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.*;

import java.util.HashMap;
import java.util.List;

public class BuiltinEivCompatIntegration implements IEivCompatIntegration {


    @Override
    public void onInitialize() {

        CompatItemView.addReloadCallback(() -> {

            Registry<Potion> potionRegistry = CompatRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.POTION);

            potionRegistry.forEach(potion -> {
                CompatItemView.addStackSensitive(PotionContents.createItemStack(Items.POTION, potionRegistry.wrapAsHolder(potion)));
                CompatItemView.addStackSensitive(PotionContents.createItemStack(Items.SPLASH_POTION, potionRegistry.wrapAsHolder(potion)));
                CompatItemView.addStackSensitive(PotionContents.createItemStack(Items.LINGERING_POTION, potionRegistry.wrapAsHolder(potion)));

                if (CompatRecipeManager.INSTANCE.getServer().potionBrewing().isBrewablePotion(potionRegistry.wrapAsHolder(potion))) {
                    net.minecraft.world.item.ItemStack tipped = new net.minecraft.world.item.ItemStack(Items.TIPPED_ARROW);
                    tipped.set(DataComponents.POTION_CONTENTS, new PotionContents(potionRegistry.wrapAsHolder(potion)));
                    CompatItemView.addStackSensitive(tipped);
                }
            });


            Registry<Enchantment> enchantmentRegistry = ((CraftServer)Bukkit.getServer()).getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            enchantmentRegistry.forEach(enchantment -> {
                for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {

                    ItemStack enchantedBook = EnchantmentHelper.createBook(new EnchantmentInstance(enchantmentRegistry.wrapAsHolder(enchantment), i));
                    CompatItemView.addStackSensitive(enchantedBook);

                }
            });

        });


        //Shaped
        CompatItemView.addRecipeProvider(recipeList -> {

            Bukkit.getServer().recipeIterator().forEachRemaining(recipe -> {

                if (recipe instanceof ShapedRecipe shapedRecipe) {

                    HashMap<Integer, RecipeChoice> choices = new HashMap<>();

                    int height = shapedRecipe.getShape().length;
                    int width = shapedRecipe.getShape()[0].length();

                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {

                            char c = shapedRecipe.getShape()[y].charAt(x);
                            if (c == ' ') continue;

                            choices.put(x + y * 3, shapedRecipe.getChoiceMap().get(c));

                        }
                    }


                    recipeList.add(new CompatShapedRecipe(width, height, choices, CraftItemStack.asNMSCopy(shapedRecipe.getResult())));
                }

            });

        });

        //Shapeless
        CompatItemView.addRecipeProvider(recipeList -> {

            Bukkit.getServer().recipeIterator().forEachRemaining(recipe -> {
                if (recipe instanceof ShapelessRecipe shapelessRecipe)
                    recipeList.add(new CompatShapelessRecipe(shapelessRecipe.getChoiceList(), CraftItemStack.asNMSCopy(shapelessRecipe.getResult())));
            });
        });

        //Smelting
        CompatItemView.addRecipeProvider(recipeList -> {

            Bukkit.getServer().recipeIterator().forEachRemaining(recipe -> {

                if(recipe instanceof FurnaceRecipe furnaceRecipe)
                    recipeList.add(new CompatSmeltingRecipe(furnaceRecipe.getInputChoice(), CraftItemStack.asNMSCopy(furnaceRecipe.getResult())));

            });
        });

        //Blasting
        CompatItemView.addRecipeProvider(recipeList -> {

            Bukkit.getServer().recipeIterator().forEachRemaining(recipe -> {

                if(recipe instanceof BlastingRecipe blastingRecipe)
                    recipeList.add(new CompatBlastingRecipe(blastingRecipe.getInputChoice(), CraftItemStack.asNMSCopy(blastingRecipe.getResult())));

            });
        });

        //Smoking
        CompatItemView.addRecipeProvider(recipeList -> {

            Bukkit.getServer().recipeIterator().forEachRemaining(recipe -> {

                if(recipe instanceof SmokingRecipe smokingRecipe)
                    recipeList.add(new CompatSmokingRecipe(smokingRecipe.getInputChoice(), CraftItemStack.asNMSCopy(smokingRecipe.getResult())));

            });
        });

        //Stonecutting
        CompatItemView.addRecipeProvider(recipeList -> {

            Bukkit.getServer().recipeIterator().forEachRemaining(recipe -> {

                if(recipe instanceof StonecuttingRecipe stonecuttingRecipe)
                    recipeList.add(new CompatStonecutterRecipe(stonecuttingRecipe.getInputChoice(), CraftItemStack.asNMSCopy(stonecuttingRecipe.getResult())));

            });

        });



    }


}

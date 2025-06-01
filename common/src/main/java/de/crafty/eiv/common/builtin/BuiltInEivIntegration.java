package de.crafty.eiv.common.builtin;

import de.crafty.eiv.common.api.IExtendedItemViewIntegration;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.builtin.blasting.BlastingServerRecipe;
import de.crafty.eiv.common.builtin.brewing.BrewingServerRecipe;
import de.crafty.eiv.common.builtin.brewing.BrewingViewRecipe;
import de.crafty.eiv.common.builtin.burning.BurningServerRecipe;
import de.crafty.eiv.common.builtin.burning.BurningViewRecipe;
import de.crafty.eiv.common.builtin.campfire.CampfireServerRecipe;
import de.crafty.eiv.common.builtin.shaped.ShapedServerRecipe;
import de.crafty.eiv.common.builtin.shapeless.ShapelessServerRecipe;
import de.crafty.eiv.common.builtin.smelting.SmeltingServerRecipe;
import de.crafty.eiv.common.builtin.smithing.SmithingServerRecipe;
import de.crafty.eiv.common.builtin.smoking.SmokingServerRecipe;
import de.crafty.eiv.common.builtin.stonecutting.StonecutterServerRecipe;
import de.crafty.eiv.common.mixin.world.item.alchemy.PotionBrewingAccessor;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.builtin.blasting.BlastingViewRecipe;
import de.crafty.eiv.common.builtin.campfire.CampfireViewRecipe;
import de.crafty.eiv.common.builtin.shaped.CraftingViewRecipe;
import de.crafty.eiv.common.builtin.shapeless.ShapelessViewRecipe;
import de.crafty.eiv.common.builtin.smelting.SmeltingViewRecipe;
import de.crafty.eiv.common.builtin.smithing.SmithingViewRecipe;
import de.crafty.eiv.common.builtin.smoking.SmokingViewRecipe;
import de.crafty.eiv.common.builtin.stonecutting.StonecutterViewRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.FuelValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.crafty.eiv.common.CommonEIV.*;

public class BuiltInEivIntegration implements IExtendedItemViewIntegration {

    public static final ResourceLocation WIDGETS = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/eiv_widgets.png");


    @Override
    public void onIntegrationInitialize() {

        ItemView.excludeItem(Items.AIR);

        BuiltInRegistries.POTION.forEach(potion -> {
            ItemView.addStackSensitive(PotionContents.createItemStack(Items.POTION, Holder.direct(potion)));
            ItemView.addStackSensitive(PotionContents.createItemStack(Items.SPLASH_POTION, Holder.direct(potion)));
            ItemView.addStackSensitive(PotionContents.createItemStack(Items.LINGERING_POTION, Holder.direct(potion)));
        });

        //providers
        ItemView.addRecipeProvider(recipeList -> {
            FuelValues fuelValues = ServerRecipeManager.INSTANCE.getServer().fuelValues();
            fuelValues.fuelItems().forEach(item -> {
                recipeList.add(new BurningServerRecipe(item, fuelValues.burnDuration(new ItemStack(item))));
            });

        });


        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMELTING).forEach(recipe -> {
                recipeList.add(new SmeltingServerRecipe(recipe.input(), recipe.result));
            });
        });

        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.BLASTING).forEach(recipe -> {
                recipeList.add(new BlastingServerRecipe(recipe.input(), recipe.result));
            });
        });

        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMOKING).forEach(recipe -> {
                recipeList.add(new SmokingServerRecipe(recipe.input(), recipe.result));
            });
        });

        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.CRAFTING).forEach(recipe -> {
                if (recipe instanceof ShapelessRecipe shapelessRecipe)
                    recipeList.add(new ShapelessServerRecipe(shapelessRecipe.ingredients, shapelessRecipe.result));

                if (recipe instanceof ShapedRecipe shapedRecipe) {

                    HashMap<Integer, Ingredient> ingredients = new HashMap<>();

                    int i = 0;
                    for (int y = 0; y < 3; y++) {
                        for (int x = 0; x < 3; x++) {

                            if (x >= shapedRecipe.getWidth() || y >= shapedRecipe.getHeight()) {
                                continue;
                            }

                            if (shapedRecipe.getIngredients().get(i).isPresent())
                                ingredients.put(x + y * 3, shapedRecipe.getIngredients().get(i).get());

                            i++;
                        }
                    }

                    recipeList.add(new ShapedServerRecipe(shapedRecipe.getWidth(), shapedRecipe.getHeight(), ingredients, shapedRecipe.result));
                }
            });
        });

        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.CAMPFIRE_COOKING).forEach(campfireCookingRecipe -> {
                recipeList.add(new CampfireServerRecipe(campfireCookingRecipe.input(), campfireCookingRecipe.result));
            });
        });

        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.STONECUTTING).forEach(stonecutterRecipe -> {
                recipeList.add(new StonecutterServerRecipe(stonecutterRecipe.input(), stonecutterRecipe.result));
            });
        });

        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMITHING).forEach(smithingRecipe -> {

                if(smithingRecipe instanceof SmithingTrimRecipe trimRecipe)
                    recipeList.add(new SmithingServerRecipe(true, trimRecipe.baseIngredient(), trimRecipe.templateIngredient().orElse(null),trimRecipe.additionIngredient().orElse(null), trimRecipe.pattern.value()));

                if(smithingRecipe instanceof SmithingTransformRecipe transformRecipe)
                    recipeList.add(new SmithingServerRecipe(false, transformRecipe.baseIngredient(), transformRecipe.templateIngredient().orElse(null), transformRecipe.additionIngredient().orElse(null), null));

            });
        });

        ItemView.addRecipeProvider(recipeList -> {

            PotionBrewing potionBrewing = ServerRecipeManager.INSTANCE.getServer().potionBrewing();
            List<PotionBrewing.Mix<Potion>> potionMixes = ((PotionBrewingAccessor) potionBrewing).getPotionMixes();
            List<PotionBrewing.Mix<Item>> containerMixes = ((PotionBrewingAccessor) potionBrewing).getContainerMixes();

            containerMixes.forEach(itemMix -> {
                recipeList.add(new BrewingServerRecipe(new ItemStack(itemMix.to().value()), itemMix.ingredient(), new ItemStack(itemMix.from().value())));
            });

            potionMixes.forEach(potionMix -> {
                recipeList.add(new BrewingServerRecipe(PotionContents.createItemStack(Items.POTION, potionMix.to()), potionMix.ingredient(), PotionContents.createItemStack(Items.POTION, potionMix.from())));
                recipeList.add(new BrewingServerRecipe(PotionContents.createItemStack(Items.SPLASH_POTION, potionMix.to()), potionMix.ingredient(), PotionContents.createItemStack(Items.SPLASH_POTION, potionMix.from())));
                recipeList.add(new BrewingServerRecipe(PotionContents.createItemStack(Items.LINGERING_POTION, potionMix.to()), potionMix.ingredient(), PotionContents.createItemStack(Items.LINGERING_POTION, potionMix.from())));

            });

        });

        //Wrapper
        ItemView.registerRecipeWrapper(BurningServerRecipe.TYPE, unwrapped -> List.of(new BurningViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(SmeltingServerRecipe.TYPE, unwrapped -> List.of(new SmeltingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(BlastingServerRecipe.TYPE, unwrapped -> List.of(new BlastingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(SmokingServerRecipe.TYPE, unwrapped -> List.of(new SmokingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(ShapelessServerRecipe.TYPE, unwrapped -> List.of(new ShapelessViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(ShapedServerRecipe.TYPE, unwrapped -> List.of(new CraftingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(CampfireServerRecipe.TYPE, unwrapped -> List.of(new CampfireViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(StonecutterServerRecipe.TYPE, unwrapped -> List.of(new StonecutterViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(SmithingServerRecipe.TYPE, unwrapped -> {
            List<SmithingViewRecipe> recipes = new ArrayList<>();

            SlotContent.of(unwrapped.getTemplate()).getValidContents().forEach(templateStack -> {

                SlotContent.of(unwrapped.getBase()).getValidContents().forEach(baseStack -> {
                    recipes.add(new SmithingViewRecipe(unwrapped.isTrim(), unwrapped.getAddition(), baseStack, templateStack, unwrapped.getPattern()));
                });

            });

            return recipes;
        });
        ItemView.registerRecipeWrapper(BrewingServerRecipe.TYPE, unwrapped -> List.of(new BrewingViewRecipe(unwrapped)));

    }
}

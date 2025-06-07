package de.crafty.eiv.common.builtin;

import com.mojang.datafixers.util.Either;
import de.crafty.eiv.common.api.IExtendedItemViewIntegration;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
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
import de.crafty.eiv.common.builtin.tipped_arrow.TippedArrowServerRecipe;
import de.crafty.eiv.common.builtin.transmute.TransmuteServerRecipe;
import de.crafty.eiv.common.builtin.villager.VillagerServerRecipe;
import de.crafty.eiv.common.builtin.villager.VillagerViewRecipe;
import de.crafty.eiv.common.mixin.world.entity.npc.*;
import de.crafty.eiv.common.mixin.world.item.alchemy.PotionBrewingAccessor;
import de.crafty.eiv.common.mixin.world.item.crafting.IngredientAccessor;
import de.crafty.eiv.common.mixin.world.item.crafting.TransmuteRecipeAccessor;
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
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCost;
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCostWithDifficulty;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.level.block.entity.FuelValues;

import java.util.*;
import java.util.stream.Collectors;

import static de.crafty.eiv.common.CommonEIV.*;

public class BuiltInEivIntegration implements IExtendedItemViewIntegration {

    public static final ResourceLocation WIDGETS = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/eiv_widgets.png");


    @Override
    public void onIntegrationInitialize() {


        ItemView.excludeItem(Items.AIR);

        ItemView.addReloadCallback(() -> {

            Registry<Potion> potionRegistry = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.POTION);

            potionRegistry.forEach(potion -> {
                ItemView.addStackSensitive(PotionContents.createItemStack(Items.POTION, potionRegistry.wrapAsHolder(potion)));
                ItemView.addStackSensitive(PotionContents.createItemStack(Items.SPLASH_POTION, potionRegistry.wrapAsHolder(potion)));
                ItemView.addStackSensitive(PotionContents.createItemStack(Items.LINGERING_POTION, potionRegistry.wrapAsHolder(potion)));

                if (ServerRecipeManager.INSTANCE.getServer().potionBrewing().isBrewablePotion(potionRegistry.wrapAsHolder(potion))) {
                    ItemStack tipped = new ItemStack(Items.TIPPED_ARROW);
                    tipped.set(DataComponents.POTION_CONTENTS, new PotionContents(potionRegistry.wrapAsHolder(potion)));
                    ItemView.addStackSensitive(tipped);
                }
            });


            Registry<Enchantment> enchantmentRegistry = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            enchantmentRegistry.forEach(enchantment -> {
                for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {

                    ItemStack enchantedBook = EnchantmentHelper.createBook(new EnchantmentInstance(enchantmentRegistry.wrapAsHolder(enchantment), i));
                    ItemView.addStackSensitive(enchantedBook);

                }
            });

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

                if (recipe instanceof TransmuteRecipe) {
                    TransmuteRecipeAccessor accessor = (TransmuteRecipeAccessor) recipe;

                    List<ItemStack> results = new ArrayList<>();

                    Either<TagKey<Item>, List<Holder<Item>>> ingredientContent = ((IngredientAccessor) (Object) accessor.getInput()).getValues().unwrap();

                    List<Item> ingredients = new ArrayList<>();

                    if (ingredientContent.left().isPresent()) {
                        SlotContent.getItemsFromTag(ingredientContent.left().get()).ifPresent(holders -> {
                            holders.forEach(holder -> ingredients.add(holder.value()));
                        });
                    }
                    if (ingredientContent.right().isPresent())
                        ingredients.addAll(ingredientContent.right().get().stream().map(Holder::value).toList());


                    ingredients.forEach(ingredient -> {
                            results.add(accessor.getResult().apply(new ItemStack(ingredient)));
                    });

                    if (!ingredients.isEmpty() && !results.isEmpty())
                        recipeList.add(new TransmuteServerRecipe(accessor.getInput(), accessor.getMaterial(), results));

                }

            });

            //Tipped arrows
            Registry<Potion> potionRegistry = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.POTION);
            potionRegistry.forEach(potion -> {
                ItemStack potionStack = PotionContents.createItemStack(Items.LINGERING_POTION, potionRegistry.wrapAsHolder(potion));
                recipeList.add(new TippedArrowServerRecipe(potionStack));
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

                if (smithingRecipe instanceof SmithingTrimRecipe trimRecipe)
                    recipeList.add(new SmithingServerRecipe(true, trimRecipe.baseIngredient(), trimRecipe.templateIngredient().orElse(null), trimRecipe.additionIngredient().orElse(null), trimRecipe.pattern.value()));

                if (smithingRecipe instanceof SmithingTransformRecipe transformRecipe)
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

        ItemView.addRecipeProvider(recipeList -> {

            VillagerTrades.TRADES.forEach((profession, byProfessionLevel) -> {

                byProfessionLevel.forEach((professionLevel, itemListings) -> {
                    Arrays.stream(itemListings).toList().forEach(listing -> {

                        if (listing instanceof VillagerTrades.EmeraldForItems emeraldForItems)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.EMERALD_FOR_ITEMS, emeraldForItems)));

                        if (listing instanceof VillagerTrades.ItemsForEmeralds itemsForEmeralds)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.ITEMS_FOR_EMERALDS, itemsForEmeralds)));

                        if (listing instanceof VillagerTrades.SuspiciousStewForEmerald suspiciousStewForEmerald)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.SUSPICIOUS_STEW, suspiciousStewForEmerald)));

                        if (listing instanceof VillagerTrades.EnchantBookForEmeralds enchantBookForEmeralds)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.ENCHANT_BOOK, enchantBookForEmeralds)));

                        if (listing instanceof VillagerTrades.TreasureMapForEmeralds treasureMapForEmeralds)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.TREASURE_MAP, treasureMapForEmeralds)));

                        if (listing instanceof VillagerTrades.TippedArrowForItemsAndEmeralds tippedArrowForItemsAndEmeralds)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.TIPPED_ARROW, tippedArrowForItemsAndEmeralds)));

                        if (listing instanceof VillagerTrades.EnchantedItemForEmeralds enchantedItemForEmeralds)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.ENCHANTED_ITEM_FOR_EMERALDS, enchantedItemForEmeralds)));

                        if (listing instanceof VillagerTrades.DyedArmorForEmeralds dyedArmorForEmeralds)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.DYED_ARMOR, dyedArmorForEmeralds)));

                        if (listing instanceof VillagerTrades.ItemsAndEmeraldsToItems itemsAndEmeraldsToItems)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.ITEMS_AND_EMERALDS_TO_ITEMS, itemsAndEmeraldsToItems)));

                        if (listing instanceof VillagerTrades.EmeraldsForVillagerTypeItem emeraldsForVillagerTypeItem)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.EMERALDS_FOR_VILLAGER_TYPE, emeraldsForVillagerTypeItem)));

                        if (listing instanceof VillagerTrades.TypeSpecificTrade typeSpecificTrade)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.TYPE_SPECIFIC, typeSpecificTrade)));
                    });
                });

            });

        });

        //Wrapper
        ItemView.registerRecipeWrapper(BurningServerRecipe.TYPE, unwrapped -> List.of(new BurningViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(SmeltingServerRecipe.TYPE, unwrapped -> List.of(new SmeltingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(BlastingServerRecipe.TYPE, unwrapped -> List.of(new BlastingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(SmokingServerRecipe.TYPE, unwrapped -> List.of(new SmokingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(ShapelessServerRecipe.TYPE, unwrapped -> List.of(new ShapelessViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(ShapedServerRecipe.TYPE, unwrapped -> List.of(new CraftingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(TransmuteServerRecipe.TYPE, unwrapped -> List.of(new ShapelessViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(TippedArrowServerRecipe.TYPE, unwrapped -> List.of(new CraftingViewRecipe(unwrapped)));
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
        ItemView.registerRecipeWrapper(VillagerServerRecipe.TYPE, unwrapped -> {
            return unwrapped.getOffers().stream().map(VillagerViewRecipe::new).toList();
        });
    }


    private void addVillagerRecipes() {

        ItemView.addRecipeProvider(recipeList -> {

            VillagerTrades.TRADES.forEach((profession, byProfessionLevel) -> {

                byProfessionLevel.forEach((professionLevel, itemListings) -> {
                    Arrays.stream(itemListings).toList().forEach(listing -> {

                        //this.itemListingToRecipe(profession, professionLevel, null, listing, recipeList);

                    });
                });

            });

        });

    }


    /*private void itemListingToRecipe(ResourceKey<VillagerProfession> profession, int professionLevel, ResourceKey<VillagerType> requiredType, VillagerTrades.ItemListing listing, List<IEivServerRecipe> recipeList) {


        if (listing instanceof VillagerTrades.TypeSpecificTrade typeSpecificTrade) {
            typeSpecificTrade.trades().forEach((villagerTypeResourceKey, itemListing) -> {
                //this.itemListingToRecipe(profession, professionLevel, villagerTypeResourceKey, listing, recipeList);
            });
        }

    }*/

}

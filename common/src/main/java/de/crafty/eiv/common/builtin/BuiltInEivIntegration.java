package de.crafty.eiv.common.builtin;

import de.crafty.eiv.common.api.IExtendedItemViewIntegration;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.builtin.blasting.BlastingViewRecipe;
import de.crafty.eiv.common.builtin.brewing.BrewingViewRecipe;
import de.crafty.eiv.common.builtin.burning.BurningViewRecipe;
import de.crafty.eiv.common.builtin.campfire.CampfireViewRecipe;
import de.crafty.eiv.common.builtin.shaped.CraftingViewRecipe;
import de.crafty.eiv.common.builtin.shapeless.ShapelessViewRecipe;
import de.crafty.eiv.common.builtin.smelting.SmeltingViewRecipe;
import de.crafty.eiv.common.builtin.smithing.SmithingViewRecipe;
import de.crafty.eiv.common.builtin.smoking.SmokingViewRecipe;
import de.crafty.eiv.common.builtin.stonecutting.StonecutterViewRecipe;
import de.crafty.eiv.common.builtin.villager.VillagerViewRecipe;
import de.crafty.eiv.common.mixin.world.item.crafting.SmithingTransformRecipeAccessor;
import de.crafty.eiv.common.mixin.world.item.crafting.SmithingTrimRecipeAccessor;
import de.crafty.eiv.common.recipe.ClientRecipeManager;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.recipe.util.EivUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.armortrim.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import java.util.*;

import static de.crafty.eiv.common.CommonEIV.*;

public class BuiltInEivIntegration implements IExtendedItemViewIntegration {

    public static final ResourceLocation WIDGETS = new ResourceLocation(MODID, "textures/gui/eiv_widgets.png");

    //Default slot rendering
    public static final ResourceLocation DEFAULT_SLOT_TEXTURE = new ResourceLocation(MODID, "textures/gui/default_slot.png");

    private static final TagKey<Item> EXCLUDED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation("c", "hidden_from_recipe_viewers"));
    private static final TagKey<Block> EXCLUDED_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation("c", "hidden_from_recipe_viewers"));

    @Override
    public void onIntegrationInitialize() {


        ItemView.addClientReloadCallback(() -> {

            BuiltInRegistries.BLOCK.getTag(EXCLUDED_BLOCKS).ifPresent(blocks -> blocks.stream().filter(Holder::isBound).filter(Holder::isBound).map(Holder::value).forEach(block -> ItemView.excludeItem(block.asItem())));
            BuiltInRegistries.ITEM.getTag(EXCLUDED_ITEMS).ifPresent(items -> items.stream().filter(Holder::isBound).filter(Holder::isBound).map(Holder::value).forEach(ItemView::excludeItem));

        });

        ItemView.addClientReloadCallback(() -> {

            BuiltInRegistries.POTION.forEach(potion -> {
                ItemView.addStackSensitive(PotionUtils.setPotion(new ItemStack(Items.POTION), potion));
                ItemView.addStackSensitive(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
                ItemView.addStackSensitive(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), potion));


                if (PotionBrewing.isBrewablePotion(potion)) {
                    ItemStack tipped = new ItemStack(Items.TIPPED_ARROW);
                    PotionUtils.setPotion(tipped, potion);
                    ItemView.addStackSensitive(tipped);
                }
            });


            BuiltInRegistries.ENCHANTMENT.forEach(enchantment -> {
                for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {

                    HashMap<Enchantment, Integer> enchantments = new HashMap<>();
                    enchantments.put(enchantment, i);

                    ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
                    EnchantmentHelper.setEnchantments(enchantments, enchantedBook);

                    ItemView.addStackSensitive(enchantedBook);

                }
            });

        });

        //providers

        //TODO reimplement entity loot + create api for server dependant recipes (like loot tables etc...)

        //Burning
        ItemView.addRecipeProvider(recipeList -> {

            AbstractFurnaceBlockEntity.getFuel().forEach((item, burnTime) -> {
                recipeList.add(new BurningViewRecipe(item, burnTime));
            });

        });

        //Smelting
        ItemView.addRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getVanillaRecipeManager().getAllRecipesFor(RecipeType.SMELTING).forEach(recipe -> {
                recipeList.add(new SmeltingViewRecipe(recipe));
            });
        });

        //Blasting
        ItemView.addRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getVanillaRecipeManager().getAllRecipesFor(RecipeType.BLASTING).forEach(recipe -> {
                recipeList.add(new BlastingViewRecipe(recipe));
            });
        });

        //Smoking
        ItemView.addRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getVanillaRecipeManager().getAllRecipesFor(RecipeType.SMOKING).forEach(recipe -> {
                recipeList.add(new SmokingViewRecipe(recipe));
            });
        });

        //Crafting
        ItemView.addRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getVanillaRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).forEach(recipe -> {
                if (recipe instanceof ShapelessRecipe shapelessRecipe)
                    recipeList.add(new ShapelessViewRecipe(shapelessRecipe));


                if (recipe instanceof ShapedRecipe shapedRecipe) {

                    HashMap<Integer, Ingredient> ingredients = new HashMap<>();

                    int i = 0;
                    for (int y = 0; y < 3; y++) {
                        for (int x = 0; x < 3; x++) {

                            if (x >= shapedRecipe.getWidth() || y >= shapedRecipe.getHeight()) {
                                continue;
                            }

                            if (!shapedRecipe.getIngredients().get(i).isEmpty())
                                ingredients.put(x + y * 3, shapedRecipe.getIngredients().get(i));

                            i++;
                        }
                    }

                    recipeList.add(new CraftingViewRecipe(shapedRecipe));
                }

                /*if (recipe instanceof TransmuteRecipe) {
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

                }*/

            });

            //Tipped arrows
            /*Registry<Potion> potionRegistry = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.POTION);
            potionRegistry.forEach(potion -> {
                recipeList.add(new TippedArrowServerRecipe(potion));
            });*/
        });

        //Campfire
        ItemView.addRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getVanillaRecipeManager().getAllRecipesFor(RecipeType.CAMPFIRE_COOKING).forEach(campfireCookingRecipe -> {
                recipeList.add(new CampfireViewRecipe(campfireCookingRecipe));
            });
        });

        //Stonecutting
        ItemView.addRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getVanillaRecipeManager().getAllRecipesFor(RecipeType.STONECUTTING).forEach(stonecutterRecipe -> {
                recipeList.add(new StonecutterViewRecipe(stonecutterRecipe));
            });
        });

        //Smithing
        ItemView.addRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getVanillaRecipeManager().getAllRecipesFor(RecipeType.SMITHING).forEach(smithingRecipe -> {

                if (smithingRecipe instanceof SmithingTrimRecipe trimRecipe) {
                    SmithingTrimRecipeAccessor accessor = (SmithingTrimRecipeAccessor) trimRecipe;

                    SlotContent.of(accessor.getBase()).getValidContents().forEach(base -> {
                        SlotContent.of(accessor.getTemplate()).getValidContents().forEach(templateStack -> {

                            if(Minecraft.getInstance().level == null)
                                return;

                            recipeList.add(new SmithingViewRecipe(smithingRecipe.getId(), accessor.getAddition(), base, templateStack, ItemStack.EMPTY, true));
                        });

                    });
                }

                if(smithingRecipe instanceof SmithingTransformRecipe transformRecipe) {
                    SmithingTransformRecipeAccessor accessor = (SmithingTransformRecipeAccessor) transformRecipe;

                    SlotContent.of(accessor.getBase()).getValidContents().forEach(base -> {
                        SlotContent.of(accessor.getTemplate()).getValidContents().forEach(templateStack -> {
                            recipeList.add(new SmithingViewRecipe(smithingRecipe.getId(), accessor.getAddition(), base, templateStack, accessor.getResult(), false));
                        });
                    });
                }

            });
        });

        //Brewing
        ItemView.addRecipeProvider(recipeList -> {


            PotionBrewing.CONTAINER_MIXES.forEach(itemMix -> {
                recipeList.add(new BrewingViewRecipe(EivUtil.uniqueIdFromItemMix(itemMix), new ItemStack(itemMix.to), itemMix.ingredient, new ItemStack(itemMix.from)));
            });

            PotionBrewing.POTION_MIXES.forEach(potionMix -> {
                recipeList.add(new BrewingViewRecipe(EivUtil.uniqueIdFromPotionMix(potionMix), PotionUtils.setPotion(new ItemStack(Items.POTION), potionMix.to), potionMix.ingredient, PotionUtils.setPotion(new ItemStack(Items.POTION), potionMix.from)));
                recipeList.add(new BrewingViewRecipe(EivUtil.uniqueIdFromPotionMix(potionMix), PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potionMix.to), potionMix.ingredient, PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potionMix.from)));
                recipeList.add(new BrewingViewRecipe(EivUtil.uniqueIdFromPotionMix(potionMix), PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), potionMix.to), potionMix.ingredient, PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), potionMix.from)));

            });

        });

        //Trading
        ItemView.addRecipeProvider(recipeList -> {

            VillagerTrades.TRADES.forEach((profession, byProfessionLevel) -> {

                byProfessionLevel.forEach((professionLevel, itemListings) -> {
                    Arrays.stream(itemListings).toList().forEach(listing -> {

                        if (listing instanceof VillagerTrades.EmeraldForItems emeraldForItems)
                            recipeList.add(new VillagerViewRecipe(VillagerViewRecipe.VillagerOffer.of(profession, professionLevel, emeraldForItems)));

                        if (listing instanceof VillagerTrades.ItemsForEmeralds itemsForEmeralds)
                            recipeList.add(new VillagerViewRecipe(VillagerViewRecipe.VillagerOffer.of(profession, professionLevel, itemsForEmeralds)));

                        if (listing instanceof VillagerTrades.SuspiciousStewForEmerald suspiciousStewForEmerald)
                            recipeList.add(new VillagerViewRecipe(VillagerViewRecipe.VillagerOffer.of(profession, professionLevel, suspiciousStewForEmerald)));

                        if (listing instanceof VillagerTrades.EnchantBookForEmeralds enchantBookForEmeralds)
                            recipeList.addAll(VillagerViewRecipe.VillagerOffer.of(profession, professionLevel, enchantBookForEmeralds).stream().map(VillagerViewRecipe::new).toList());

                        if (listing instanceof VillagerTrades.TreasureMapForEmeralds treasureMapForEmeralds)
                            recipeList.add(new VillagerViewRecipe(VillagerViewRecipe.VillagerOffer.of(profession, professionLevel, treasureMapForEmeralds)));

                        if (listing instanceof VillagerTrades.TippedArrowForItemsAndEmeralds tippedArrowForItemsAndEmeralds)
                            recipeList.add(new VillagerViewRecipe(VillagerViewRecipe.VillagerOffer.of(profession, professionLevel, tippedArrowForItemsAndEmeralds)));

                        if (listing instanceof VillagerTrades.EnchantedItemForEmeralds enchantedItemForEmeralds)
                            recipeList.add(new VillagerViewRecipe(VillagerViewRecipe.VillagerOffer.of(profession, professionLevel, enchantedItemForEmeralds)));

                        if (listing instanceof VillagerTrades.DyedArmorForEmeralds dyedArmorForEmeralds)
                            recipeList.add(new VillagerViewRecipe(VillagerViewRecipe.VillagerOffer.of(profession, professionLevel, dyedArmorForEmeralds)));

                        if (listing instanceof VillagerTrades.ItemsAndEmeraldsToItems itemsAndEmeraldsToItems)
                            recipeList.add(new VillagerViewRecipe(VillagerViewRecipe.VillagerOffer.of(profession, professionLevel, itemsAndEmeraldsToItems)));

                        if (listing instanceof VillagerTrades.EmeraldsForVillagerTypeItem emeraldsForVillagerTypeItem)
                            recipeList.addAll(VillagerViewRecipe.VillagerOffer.of(profession, professionLevel, emeraldsForVillagerTypeItem).stream().map(VillagerViewRecipe::new).toList());

                    });
                });

            });

        });

    }


}

package de.crafty.eiv.common.api.recipe;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.recipe.ItemViewRecipes;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ItemView {

    private static final List<Item> EXCLUDED = new ArrayList<>();
    private static final HashMap<Item, List<StackSensitive>> STACK_SENSITIVE = new HashMap<>();
    private static final List<ReloadCallback> RELOAD_CALLBACKS = new ArrayList<>();

    public static void addRecipeProvider(ItemViewRecipes.ServerRecipeProvider provider) {
        ItemViewRecipes.INSTANCE.addRecipeProvider(provider);
    }

    public static <T extends IEivServerRecipe> void registerRecipeWrapper(EivRecipeType<T> recipeType, ItemViewRecipes.ClientRecipeWrapper<T> wrapper) {
        ItemViewRecipes.INSTANCE.registerRecipeWrapper(recipeType, wrapper);
    }


    public static void excludeItem(Item item) {
        excludeItems(item);
    }

    public static void excludeItems(Item... items) {
        Arrays.stream(items).filter(item -> !EXCLUDED.contains(item)).forEach(EXCLUDED::add);
    }

    public static void addStackSensitive(ItemStack stack, ResourceLocation validatorId) {
        List<StackSensitive> present = STACK_SENSITIVE.getOrDefault(stack.getItem(), new ArrayList<>());
        present.add(new StackSensitive(stack, validatorId));
        STACK_SENSITIVE.put(stack.getItem(), present);
    }


    public static HashMap<Item, List<StackSensitive>> getStackSensitive() {
        return STACK_SENSITIVE;
    }

    public static List<Item> getExcluded() {
        return EXCLUDED;
    }

    public static void addReloadCallback(ReloadCallback callback) {
        RELOAD_CALLBACKS.add(callback);
    }

    public static List<ReloadCallback> getReloadCallbacks() {
        return RELOAD_CALLBACKS;
    }

    public interface ReloadCallback {

        void onReload();
    }

    public record StackSensitive(ItemStack stack, ResourceLocation validatorId) {

        public static final StreamCodec<RegistryFriendlyByteBuf, StackSensitive> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.COMPOUND_TAG,
                stackSensitive -> EivTagUtil.encodeItemStack(stackSensitive.stack()),
                ByteBufCodecs.STRING_UTF8,
                stackSensitive -> stackSensitive.validatorId().toString(),
                (compoundTag, s) -> new StackSensitive(EivTagUtil.decodeItemStack(compoundTag), ResourceLocation.tryParse(s))
        );

        @Override
        public ItemStack stack() {
            return stack.copy();
        }

        public UniqueValidator validator() {
            return UniqueValidator.VALIDATORS.get(this.validatorId());
        }

        public interface UniqueValidator {

            HashMap<ResourceLocation, UniqueValidator> VALIDATORS = new HashMap<>();


            ResourceLocation POTION = register(ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "potion"),
                    (sensitive, stack) -> {

                        PotionContents sensitiveContent = sensitive.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                        PotionContents stackContent = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

                        return sensitiveContent.equals(stackContent);

                    }
            );

            ResourceLocation ENCHANTMENT = register(ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, "enchantment"),
                    (sensitive, stack) -> {
                        DataComponentType<ItemEnchantments> compType = sensitive.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;

                        ItemEnchantments sensitiveEnchantments = sensitive.getOrDefault(compType, ItemEnchantments.EMPTY);
                        ItemEnchantments stackEnchantments = stack.getOrDefault(compType, ItemEnchantments.EMPTY);


                        boolean bl = stackEnchantments.keySet().stream().allMatch(enchantment -> {
                            return sensitiveEnchantments.getLevel(enchantment) == stackEnchantments.getLevel(enchantment);
                        }) && sensitiveEnchantments.size() == stackEnchantments.size();

                        return bl;
                    }
            );

            static ResourceLocation register(ResourceLocation id, UniqueValidator validator) {
                VALIDATORS.put(id, validator);
                return id;
            }

            boolean isSame(ItemStack sensitive, ItemStack stack);

        }
    }
}

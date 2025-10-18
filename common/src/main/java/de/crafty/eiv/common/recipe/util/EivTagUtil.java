package de.crafty.eiv.common.recipe.util;

import com.mojang.datafixers.util.Either;
import de.crafty.eiv.common.mixin.world.item.crafting.IngredientAccessor;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Helper class for network encoding based on CompoundTags
 */
public class EivTagUtil {


    private static <T> ListTag createRegistryList(List<T> objects, DefaultedRegistry<T> registry) {
        ListTag list = new ListTag();
        objects.stream().map(t -> StringTag.valueOf(registryToString(t, registry))).forEach(list::add);
        return list;
    }

    private static <T> List<T> reconstructRegistryList(CompoundTag srcTag, String key, DefaultedRegistry<T> registry) {
        return srcTag.getListOrEmpty(key).stream().map(Tag::asString).map(s -> stringToRegistry(s.orElseThrow(), registry)).filter(Objects::nonNull).toList();
    }


    public static ItemStack decodeItemStackOnClient(CompoundTag tag) {
        return ItemStack.CODEC.parse(Minecraft.getInstance().player.level().registryAccess().createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(ItemStack.EMPTY);
    }

    public static CompoundTag encodeItemStackOnClient(ItemStack stack) {
        return ItemStack.CODEC.encode(stack, Minecraft.getInstance().player.level().registryAccess().createSerializationContext(NbtOps.INSTANCE), new CompoundTag()).mapOrElse(tag -> tag.asCompound().orElseGet(CompoundTag::new), tagError -> new CompoundTag());
    }

    public static CompoundTag encodeItemStackOnServer(ItemStack stack) {
        return ItemStack.CODEC.encode(stack, ServerRecipeManager.INSTANCE.getServer().registryAccess().createSerializationContext(NbtOps.INSTANCE), new CompoundTag()).mapOrElse(tag -> tag.asCompound().orElseGet(CompoundTag::new), tagError -> new CompoundTag());
    }

    public static ItemStack decodeItemStackOnServer(CompoundTag tag) {
        return ItemStack.CODEC.parse(ServerRecipeManager.INSTANCE.getServer().registryAccess().createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(ItemStack.EMPTY);
    }

    public static CompoundTag writeIngredient(Ingredient ingredient) {
        if (ingredient == null)
            return new CompoundTag();

        HolderSet<Item> set = ((IngredientAccessor) (Object) ingredient).getValues();

        Either<TagKey<Item>, List<Holder<Item>>> ingredientContent = set.unwrap();
        CompoundTag tag = new CompoundTag();

        if (ingredientContent.left().isPresent()) {
            tag.putString("tag", ingredientContent.left().get().location().toString());
            return tag;
        }

        if(ingredientContent.right().isEmpty())
            return new CompoundTag();

        tag.put("items", EivTagUtil.createItemList(ingredientContent.right().get().stream().filter(Holder::isBound).map(Holder::value).toList()));
        return tag;
    }

    public static Ingredient readIngredient(CompoundTag tag) {
        if (tag.isEmpty())
            return null;


        if (tag.contains("tag")) {
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, ResourceLocation.parse(tag.getStringOr("tag", "")));
            if (BuiltInRegistries.ITEM.get(tagKey).isEmpty())
                return null;

            return Ingredient.of(Objects.requireNonNull(BuiltInRegistries.ITEM.get(tagKey).get()));
        }

        List<Holder<Item>> itemList = EivTagUtil.reconstructItemList(tag, "items").stream().map(Holder::direct).toList();
        return Ingredient.of(HolderSet.direct(itemList));
    }

    //----------------- Item, Block, Fluid -----------------

    public static ListTag createItemList(List<Item> items) {
        return createRegistryList(items, BuiltInRegistries.ITEM);
    }


    public static List<Item> reconstructItemList(CompoundTag srcTag, String key) {
        return reconstructRegistryList(srcTag, key, BuiltInRegistries.ITEM);
    }

    public static ListTag createBlockList(List<Block> items) {
        return createRegistryList(items, BuiltInRegistries.BLOCK);
    }

    public static List<Block> reconstructBlockList(CompoundTag srcTag, String key) {
        return reconstructRegistryList(srcTag, key, BuiltInRegistries.BLOCK);
    }

    public static ListTag createFluidList(List<Fluid> items) {
        return createRegistryList(items, BuiltInRegistries.FLUID);
    }


    public static List<Fluid> reconstructFluidList(CompoundTag srcTag, String key) {
        return reconstructRegistryList(srcTag, key, BuiltInRegistries.FLUID);
    }

    //----------------- Custom Objects -----------------


    public static <T> ListTag writeList(List<T> list, CompoundBuilder<T> builder) {
        ListTag tagList = new ListTag();
        list.stream().map(t -> builder.buildSingle(t, new CompoundTag())).forEach(tagList::add);
        return tagList;
    }

    public static <T> List<T> readList(CompoundTag srcTag, String key, CompoundReconstructor<T> builder) {
        return srcTag.getListOrEmpty(key).stream().map(Tag::asCompound).map(compoundTag -> builder.reconstructSingle(compoundTag.orElseGet(CompoundTag::new))).toList();
    }


    //----------------- Registry-String Converter -----------------


    private static <T> String registryToString(T object, DefaultedRegistry<T> registry) {
        return registry.getKey(object).toString();
    }

    private static <T> T stringToRegistry(String string, DefaultedRegistry<T> registry) {
        if (string.isEmpty())
            return null;

        return registry.getOptional(ResourceLocation.tryParse(string)).orElse(null);
    }

    public static String itemToString(Item item) {
        return registryToString(item, BuiltInRegistries.ITEM);
    }

    public static Item itemFromString(String s) {
        return stringToRegistry(s, BuiltInRegistries.ITEM);
    }

    public static String blockToString(Block block) {
        return registryToString(block, BuiltInRegistries.BLOCK);
    }

    public static Block blockFromString(String s) {
        return stringToRegistry(s, BuiltInRegistries.BLOCK);
    }

    public static String fluidToString(Fluid fluid) {
        return registryToString(fluid, BuiltInRegistries.FLUID);
    }

    public static Fluid fluidFromString(String s) {
        return stringToRegistry(s, BuiltInRegistries.FLUID);
    }

    //----------------- Custom object builder/reconstructor -----------------


    public interface CompoundBuilder<T> {

        CompoundTag buildSingle(T origin, CompoundTag tag);

    }

    public interface CompoundReconstructor<T> {

        T reconstructSingle(CompoundTag tag);

    }
}

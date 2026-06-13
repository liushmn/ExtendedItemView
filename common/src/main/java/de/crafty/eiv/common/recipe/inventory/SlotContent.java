package de.crafty.eiv.common.recipe.inventory;

import com.mojang.datafixers.util.Either;
import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.extra.FluidStack;
import de.crafty.eiv.common.recipe.ItemViewRecipes;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SlotContent {

    private final List<ItemStack> content;
    private int current;

    private List<TagKey<Item>> itemTags;

    private ItemStack itemOrigin;
    private SlotContent.Type originType;

    private Type type;

    private SlotContent(List<ItemStack> content) {

        List<ItemStack> copied = new ArrayList<>();
        content.stream().map(ItemStack::copy).forEach(copied::add);

        this.content = copied;
        this.current = 0;

        this.itemOrigin = ItemStack.EMPTY;
        this.originType = SlotContent.Type.ANY;

        this.type = Type.INGREDIENT;

        this.itemTags = new ArrayList<>();
    }

    public SlotContent copy() {

        SlotContent copy = new SlotContent(this.content);

        copy.type = this.type;
        copy.itemOrigin = this.itemOrigin.copy();
        copy.itemTags = this.itemTags;
        copy.originType = this.originType;
        copy.current = this.current;

        return copy;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    private SlotContent bindItemTag(TagKey<Item> tag) {
        if(!this.itemTags.contains(tag))
            this.itemTags.add(tag);

        this.updateTags();
        return this;
    }


    public void bindOrigin(ItemStack stack, SlotContent.Type originType) {
        this.itemOrigin = stack.copy();
        this.originType = originType;
    }

    public int size() {
        return this.content.size();
    }

    public boolean isEmpty() {
        return this.content.stream().filter(ItemStack::isEmpty).count() == this.content.size();
    }

    public int index() {

        if (this.hasItem(this.itemOrigin.getItem()) && this.originType == this.type)
            return this.getNextMatching(this.itemOrigin);

        return this.current;
    }

    public ItemStack getByIndex(int index) {
        return this.content.isEmpty() ? ItemStack.EMPTY : this.content.get(index).copy();
    }

    public ItemStack next() {
        this.current++;
        if (this.current >= this.content.size())
            this.current = 0;

        return this.getByIndex(this.index());
    }

    public void resetPointer() {
        this.current = 0;
        this.itemOrigin = ItemStack.EMPTY;
        this.originType = SlotContent.Type.ANY;
    }

    public void pointTo(int index) {
        this.current = index;
    }


    public List<ItemStack> getValidContents() {
        return this.content;
    }

    private void updateTags() {
        if (this.itemTags().isEmpty())
            return;

        this.content.forEach(stack -> {
            CompoundTag tag = stack.getOrCreateTag();
            ListTag itemTags = new ListTag();
            this.itemTags.forEach(tagKey -> {
                itemTags.add(StringTag.valueOf(tagKey.location().toString()));
            });

            tag.put(CommonEIV.MODID + "_recipeTag", itemTags);
        });

    }

    public boolean hasItem(Item check) {
        return this.content.stream().anyMatch(stack -> stack.getItem() == check);
    }

    public int getNextMatching(ItemStack origin) {

        for (int i = this.current; i < this.content.size() + this.current; i++) {
            int index = i < this.content.size() ? i : i - this.content.size();

            ItemStack stack = this.content.get(index);

            if (stack.getItem() != origin.getItem())
                continue;

            boolean potionCheck = ItemViewRecipes.makePotionCheck(origin, stack);
            boolean enchantCheck = ItemViewRecipes.makeEnchantmentCheck(origin, stack);

            if (potionCheck && enchantCheck)
                return index;
        }

        return this.current;
    }


    public List<TagKey<Item>> itemTags() {
        return this.itemTags;
    }


    public void encodeDetails(CompoundTag tag) {

        tag.putInt("current", this.current);
        tag.putString("originType",  this.originType.name());
        tag.putString("type", this.type.name());
        tag.put("itemOrigin", EivTagUtil.encodeItemStackOnClient(this.itemOrigin));

    }

    public void decodeDetails(CompoundTag tag) {

        this.current = tag.getInt("current");
        this.originType = Type.valueOf(tag.getString("originType"));
        this.type = Type.valueOf(tag.getString("type"));
        this.itemOrigin = EivTagUtil.decodeItemStackOnClient(tag.getCompound("itemOrigin"));

    }


    public static SlotContent of() {
        return new SlotContent(List.of());
    }

    public static SlotContent of(Item item) {
        return new SlotContent(List.of(new ItemStack(item)));
    }

    public static SlotContent ofItemList(List<Item> items) {
        List<ItemStack> stacks = new ArrayList<>();
        items.forEach(item -> stacks.add(new ItemStack(item)));
        return SlotContent.of(stacks);
    }

    public static SlotContent of(FluidStack fluidStack) {
        return new SlotContent(List.of(fluidStack.createItemStack()));
    }

    public static SlotContent ofFluidList(List<FluidStack> fluidStacks) {
        List<ItemStack> stacks = new ArrayList<>();
        fluidStacks.forEach(fluidStack -> stacks.add(fluidStack.createItemStack()));
        return new SlotContent(stacks);
    }

    public static SlotContent of(ItemStack stack) {
        return new SlotContent(List.of(stack));
    }

    public static SlotContent of(List<ItemStack> stacks) {
        return new SlotContent(stacks);
    }

    public static SlotContent of(TagKey<Item> itemTag) {
        List<Item> items = new ArrayList<>();
        SlotContent.getItemsFromTag(itemTag).ifPresent(holders -> {
            holders.forEach(holder -> {
                items.add(holder.value());
            });
        });

        List<ItemStack> stacks = new ArrayList<>();
        items.forEach(item -> stacks.add(new ItemStack(item)));

        return new SlotContent(stacks).bindItemTag(itemTag);
    }

    public static SlotContent of(Ingredient ingredient) {
        if (ingredient == null)
            return SlotContent.of();

        List<ItemStack> stacks = new ArrayList<>();
        List<TagKey<Item>> tags = new ArrayList<>();

        for(Ingredient.Value value : ingredient.values){
            if(value instanceof Ingredient.ItemValue itemValue)
                stacks.addAll(itemValue.getItems());

            if(value instanceof Ingredient.TagValue tagValue){
                tags.add(tagValue.tag);
                stacks.addAll(tagValue.getItems());
            }
        }

        SlotContent slotContent = new SlotContent(stacks);
        tags.forEach(slotContent::bindItemTag);

        return slotContent;
    }

    public static Optional<HolderSet.Named<Item>> getItemsFromTag(TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.getTag(tag);
    }


    public enum Type {
        INGREDIENT,
        RESULT,
        ANY
    }

}

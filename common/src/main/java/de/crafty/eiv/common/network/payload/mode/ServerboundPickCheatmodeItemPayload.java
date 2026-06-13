package de.crafty.eiv.common.network.payload.mode;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.network.payload.ICustomEivPayload;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ServerboundPickCheatmodeItemPayload implements ICustomEivPayload {


    private ItemStack stack;
    private int amount;

    public static final ResourceLocation ID = new ResourceLocation(CommonEIV.MODID, "pick_cheatmode_item");


    public ServerboundPickCheatmodeItemPayload(ItemStack stack, int amount){
        this.stack = stack;
        this.amount = amount;
    }

    public ServerboundPickCheatmodeItemPayload(){}

    @Override
    public void writeTag(CompoundTag tag) {
        tag.put("stack", EivTagUtil.encodeItemStackOnClient(this.stack));
        tag.putInt("amount", this.amount);
    }

    @Override
    public void readTag(CompoundTag tag) {
        this.stack = EivTagUtil.decodeItemStackOnServer(tag.getCompound("stack"));
        this.amount = tag.getInt("amount");
    }

    @Override
    public ResourceLocation getIdentifier() {
        return ID;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public int getAmount() {
        return this.amount;
    }
}

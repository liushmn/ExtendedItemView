package de.crafty.eiv.common.network.payload.transfer;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.network.payload.ICustomEivPayload;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public class ServerboundTransferPayload implements ICustomEivPayload {


    private HashMap<Integer, Integer> transferMap;
    private HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots;


    public static final ResourceLocation ID = new ResourceLocation(CommonEIV.MODID, "recipe_transfer");


    public ServerboundTransferPayload(HashMap<Integer, Integer> transferMap, HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots) {
        this.transferMap = transferMap;
        this.usedPlayerSlots = usedPlayerSlots;
    }

    public ServerboundTransferPayload(){
        this(new HashMap<>(), new HashMap<>());
    }

    @Override
    public void writeTag(CompoundTag tag) {
        tag.put("mapData", this.encodeMap());
    }

    @Override
    public void readTag(CompoundTag tag) {
        this.decodeMap(tag.getCompound("mapData"));
    }

    @Override
    public ResourceLocation getIdentifier() {
        return ID;
    }


    private CompoundTag encodeMap() {
        CompoundTag encoded = new CompoundTag();

        CompoundTag transferMap = new CompoundTag();
        this.transferMap.forEach((recipeSlot, destSlot) -> {
            transferMap.putInt(String.valueOf(recipeSlot), destSlot);
        });
        encoded.put("transferMap", transferMap);

        CompoundTag usedPlayerSlots = new CompoundTag();
        this.usedPlayerSlots.forEach((recipeSlot, usedSlots) -> {

            CompoundTag playerSlotsTag = new CompoundTag();
            usedSlots.forEach((playerSlot, stack) -> {
                playerSlotsTag.put(String.valueOf(playerSlot), EivTagUtil.encodeItemStackOnClient(stack));
            });

            usedPlayerSlots.put(String.valueOf(recipeSlot), playerSlotsTag);
        });

        encoded.put("usedPlayerSlots", usedPlayerSlots);
        return encoded;
    }

    private ServerboundTransferPayload decodeMap(CompoundTag encoded) {

        HashMap<Integer, Integer> transferMap = new HashMap<>();
        CompoundTag encodedTransferMap = encoded.getCompound("transferMap");

        encodedTransferMap.getAllKeys().forEach(recipeSlot -> {
            transferMap.put(Integer.valueOf(recipeSlot), encodedTransferMap.getInt(recipeSlot));
        });

        HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots = new HashMap<>();
        CompoundTag encodedUsedPlayerSlots = encoded.getCompound("usedPlayerSlots");

        encodedUsedPlayerSlots.getAllKeys().forEach(recipeSlot -> {
            HashMap<Integer, ItemStack> usedSlots = new HashMap<>();

            CompoundTag playerSlotsTag = encodedUsedPlayerSlots.getCompound(recipeSlot);
            playerSlotsTag.getAllKeys().forEach(playerSlot -> {

                ItemStack stack = EivTagUtil.decodeItemStackOnServer(playerSlotsTag.getCompound(playerSlot));
                usedSlots.put(Integer.valueOf(playerSlot), stack);
            });

            usedPlayerSlots.put(Integer.valueOf(recipeSlot), usedSlots);
        });

        return new ServerboundTransferPayload(transferMap, usedPlayerSlots);
    }

    public HashMap<Integer, Integer> getTransferMap() {
        return this.transferMap;
    }

    public HashMap<Integer, HashMap<Integer, ItemStack>> getUsedPlayerSlots() {
        return this.usedPlayerSlots;
    }
}

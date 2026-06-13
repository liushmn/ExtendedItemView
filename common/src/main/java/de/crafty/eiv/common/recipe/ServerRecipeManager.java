package de.crafty.eiv.common.recipe;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.network.EivNetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;

//TODO block incoming requests while update sending
public class ServerRecipeManager {

    public static final ServerRecipeManager INSTANCE = new ServerRecipeManager();



    private MinecraftServer server;

    private ServerRecipeManager() {

    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    /**
     * @param player          The player
     * @param transferMap     The transfer map
     * @param usedPlayerSlots The player slots
     * @return Returns whether the current crafting container recipe is the same as the recipe quick-crafted by the player
     */
    private boolean hasSameRecipeInsideContainer(ServerPlayer player, HashMap<Integer, Integer> transferMap, HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots) {

        for (int recipeSlotId : transferMap.keySet()) {
            int destSlotId = transferMap.get(recipeSlotId);

            Slot destSlot = player.containerMenu.getSlot(destSlotId);

            ItemStack destStack = destSlot.getItem();
            ItemStack requiredStack = usedPlayerSlots.getOrDefault(recipeSlotId, new HashMap<>()).values().stream().findFirst().orElse(ItemStack.EMPTY);

            if (!destStack.isEmpty() && (destStack.getCount() >= destStack.getMaxStackSize() || !ItemStack.isSameItemSameTags(destStack, requiredStack)))
                return false;

        }

        return true;
    }


    //Transfer
    public void performRecipeTransfer(ServerPlayer player, HashMap<Integer, Integer> transferMap, HashMap<Integer, HashMap<Integer, ItemStack>> usedPlayerSlots) {

        List<ItemStack> stacks = new ArrayList<>();

        //Only accepts filled crafting containers when there is the same recipe inside
        //Otherwise: Clear the content to avoid conflicts
        if (!this.hasSameRecipeInsideContainer(player, transferMap, usedPlayerSlots)) {
            player.containerMenu.slots.forEach(slot -> {
                if (!slot.getItem().isEmpty() && transferMap.containsValue(slot.index))
                    stacks.add(slot.remove(slot.getItem().getCount()));
            });
        }


        //Actual item transfer
        transferMap.forEach((recipeSlot, destSlotId) -> {

            HashMap<Integer, ItemStack> usedSlots = usedPlayerSlots.getOrDefault(recipeSlot, new HashMap<>());
            Slot destSlot = player.containerMenu.getSlot(destSlotId);

            usedSlots.forEach((playerSlot, stack) -> {
                ItemStack currentInDest = destSlot.getItem();

                if (currentInDest.isEmpty()) {
                    destSlot.set(player.containerMenu.getSlot(playerSlot).remove(stack.getCount()));
                } else {
                    destSlot.set(currentInDest.copyWithCount(currentInDest.getCount() + player.containerMenu.getSlot(playerSlot).remove(Math.min(stack.getCount(), currentInDest.getMaxStackSize() - currentInDest.getCount())).getCount()));
                }

            });


        });

        //Add cached items back to inventory
        stacks.forEach(player::addItem);

    }

}

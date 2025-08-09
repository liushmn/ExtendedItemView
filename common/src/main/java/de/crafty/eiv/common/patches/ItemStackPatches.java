package de.crafty.eiv.common.patches;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemStackPatches {
    private static final Logger LOGGER = Logger.getLogger(ItemStackPatches.class.getName());
    public static Optional<ItemStack> parse(RegistryAccess registryAccess, CompoundTag compoundTag) {
        RegistryOps<Tag> ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);

        return ItemStack.CODEC.parse(ops, compoundTag).resultOrPartial(error -> LOGGER.log(Level.SEVERE, String.format("Tried to load invalid item: '%s'", error)));
    }
}
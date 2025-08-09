package de.crafty.eiv.common.patches;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public interface ItemStackImpl {
    public Tag save(RegistryAccess registryAccess, CompoundTag prefix);
    public Tag save(RegistryAccess registryAccess);
}
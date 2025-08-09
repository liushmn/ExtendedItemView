package de.crafty.eiv.common.mixin.patches;

import de.crafty.eiv.common.patches.ItemStackImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackImpl {
    @Override
    public Tag save(RegistryAccess registryAccess, CompoundTag prefix) {
        ItemStack pThis = (ItemStack) (Object) this;
        if (pThis.isEmpty()) {
            throw new IllegalStateException("Cannot encode empty ItemStack");
        }
        RegistryOps<Tag> ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);
        return ItemStack.CODEC.encode(pThis, ops, prefix).getOrThrow();
    }

    @Override
    public Tag save(RegistryAccess registryAccess) {
        ItemStack pThis = (ItemStack) (Object) this;
        if (pThis.isEmpty()) {
            throw new IllegalStateException("Cannot encode empty ItemStack");
        }
        RegistryOps<Tag> ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);
        return ItemStack.CODEC.encodeStart(ops, pThis).getOrThrow();
    }
}
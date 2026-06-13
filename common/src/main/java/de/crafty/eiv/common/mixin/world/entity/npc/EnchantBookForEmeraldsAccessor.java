package de.crafty.eiv.common.mixin.world.entity.npc;

import net.minecraft.world.entity.npc.VillagerTrades;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerTrades.EnchantBookForEmeralds.class)
public interface EnchantBookForEmeraldsAccessor {

    @Accessor("villagerXp")
    int villagerXp();

}

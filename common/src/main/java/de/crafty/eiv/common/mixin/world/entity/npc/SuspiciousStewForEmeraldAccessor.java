package de.crafty.eiv.common.mixin.world.entity.npc;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.npc.VillagerTrades;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerTrades.SuspiciousStewForEmerald.class)
public interface SuspiciousStewForEmeraldAccessor {

    @Accessor("effect")
    MobEffect effect();

    @Accessor("duration")
    int duration();

    @Accessor("xp")
    int xp();

    @Accessor("priceMultiplier")
    float getPriceMultiplier();

}

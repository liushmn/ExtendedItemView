package de.crafty.eiv.common.mixin.world.entity.npc;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerTrades.ItemsForEmeralds.class)
public interface ItemsForEmeraldsAccessor {

    @Accessor("maxUses")
    int maxUses();

    @Accessor("villagerXp")
    int villagerXp();

    @Accessor("numberOfItems")
    int numberOfItems();

    @Accessor("itemStack")
    ItemStack itemStack();

    @Accessor("emeraldCost")
    int emeraldCost();

    @Accessor("priceMultiplier")
    float getPriceMultiplier();

}

package de.crafty.eiv.common.mixin.world.entity.npc;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerTrades.EmeraldForItems.class)
public interface EmeraldForItemsAccessor {

    @Accessor("villagerXp")
    int getVillagerXp();

    @Accessor("maxUses")
    int getMaxUses();

    @Accessor("cost")
    int getCost();

    @Accessor("item")
    Item getItem();

    @Accessor("priceMultiplier")
    float getPriceMultiplier();

}

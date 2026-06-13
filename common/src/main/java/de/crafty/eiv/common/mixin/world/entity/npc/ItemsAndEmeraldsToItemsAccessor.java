package de.crafty.eiv.common.mixin.world.entity.npc;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerTrades.ItemsAndEmeraldsToItems.class)
public interface ItemsAndEmeraldsToItemsAccessor {

    @Accessor("villagerXp")
    int getVillagerXp();

    @Accessor("maxUses")
    int getMaxUses();

    @Accessor("fromItem")
    ItemStack fromItem();

    @Accessor("fromCount")
    int fromCount();

    @Accessor("emeraldCost")
    int emeraldCost();

    @Accessor("toItem")
    ItemStack toItem();

    @Accessor("toCount")
    int toCount();


    @Accessor("priceMultiplier")
    float getPriceMultiplier();


}

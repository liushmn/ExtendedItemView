package de.crafty.eiv.common.mixin.world.item.trading;

import net.minecraft.core.HolderSet;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Optional;

@Mixin(VillagerTrade.class)
public interface VillagerTradeAccessor {


    @Accessor(value = "wants", remap = false)
    TradeCost wants();

    @Accessor(value = "additionalWants", remap = false)
    Optional<TradeCost> additionalWants();

    @Accessor(value = "gives", remap = false)
    ItemStackTemplate gives();

    @Accessor(value = "merchantPredicate", remap = false)
    Optional<LootItemCondition> merchantPredicate();

    @Accessor(value = "givenItemModifiers", remap = false)
    List<LootItemFunction> givenItemModifiers();

    @Accessor(value = "doubleTradePriceEnchantments", remap = false)
    Optional<HolderSet<Enchantment>> doubleTradePriceEnchantments();

    @Accessor(value = "maxUses", remap = false)
    NumberProvider maxUses();

    @Accessor(value = "xp", remap = false)
    NumberProvider xp();

}

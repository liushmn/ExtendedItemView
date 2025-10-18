package de.crafty.eiv.forge.mixin.registries;

import de.crafty.eiv.common.recipe.ItemViewRecipes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IdMappingEvent;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = GameData.class, remap = false)
public class MixinGameData {


    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "loadPersistentDataToStagingRegistry", at = @At("HEAD"))
    private static void excludeFluidItems(RegistryManager pool, RegistryManager _to, Map<ResourceLocation, IdMappingEvent.IdRemapping> remaps, Object2IntMap<ResourceLocation> missing, ResourceLocation name, ForgeRegistry.Snapshot snap, CallbackInfo ci){

        if (!name.equals(Registries.ITEM.location()))
            return;

        BuiltInRegistries.FLUID.forEach(fluid -> {

            Item fluidItem = ItemViewRecipes.INSTANCE.itemForFluid(fluid);

            if (fluidItem == Items.AIR)
                return;

            if (!snap.ids.containsKey(BuiltInRegistries.FLUID.getKey(fluid))) {
                snap.ids.put(BuiltInRegistries.FLUID.getKey(fluid), Item.getId(fluidItem));
            }

        });

    }

}

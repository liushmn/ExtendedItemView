package de.crafty.eiv.forge.mixin.registries;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.recipe.ItemViewRecipes;
import de.crafty.eiv.common.recipe.item.FluidItem;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.*;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = GameData.class, remap = false)
public class MixinGameData {


    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "loadPersistentDataToStagingRegistry", at = @At("HEAD"))
    private static void excludeFluidItems(RegistryManager pool, RegistryManager _to, Map<Identifier, IdMappingEvent.IdRemapping> remaps, Object2IntMap<Identifier> missing, Identifier name, ForgeRegistry.Snapshot snap, CallbackInfo ci) {

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


    @Redirect(method = "postRegisterEvents", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/ModLoader;postEventWrapContainerInModOrder(Lnet/minecraftforge/fml/event/IModBusEvent;)V"))
    private static <T extends IModBusEvent> void injectFluidItems(T e) {
        ModLoader.postEventWrapContainerInModOrder(e);

        RegisterEvent event = (RegisterEvent) e;

        if (!event.getRegistryKey().location().equals(Registries.ITEM.location()))
            return;

        HashMap<Fluid, Item> fluidItemMap = new HashMap<>();

        ForgeRegistries.FLUIDS.forEach(fluid -> {

            if (fluid == Fluids.EMPTY || !fluid.isSource(fluid.defaultFluidState()))
                return;

            Identifier fluidLocation = ForgeRegistries.FLUIDS.getKey(fluid);
            if(fluidLocation == null)
                return;

            if (event.getForgeRegistry() != null && event.getForgeRegistry().containsKey(fluidLocation)) {
                fluidItemMap.put(fluid, (Item) event.getForgeRegistry().getValue(fluidLocation));
                return;
            }
            if (event.getVanillaRegistry() != null && event.getVanillaRegistry().containsKey(fluidLocation)) {
                fluidItemMap.put(fluid, (Item) event.getVanillaRegistry().getValue(fluidLocation));
                return;
            }

            event.register(Registries.ITEM, itemRegisterHelper -> {
                itemRegisterHelper.register(fluidLocation, new FluidItem(fluid.defaultFluidState().createLegacyBlock().getBlock(),
                        new FluidItem.FluidItemProperties()
                                .fluid(fluid)
                                .setItemId(ResourceKey.create(Registries.ITEM, fluidLocation))
                ));
            });

        });

        ItemViewRecipes.INSTANCE.setFluidItemMap(fluidItemMap);
    }
}

package de.crafty.eiv.common;

import de.crafty.eiv.common.api.recipe.ItemViewRecipes;
import de.crafty.eiv.common.network.EivNetworkManager;
import de.crafty.eiv.common.network.IEivNetworkManager;
import de.crafty.eiv.common.recipe.item.FluidItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class CommonEIV {

    public static final String MODID = "eiv";

    public static final Logger LOGGER = LoggerFactory.getLogger("Extended ItemView");


    public static EivNetworkManager networkManager(){
        return EivNetworkManager.INSTANCE;
    }

    public static void buildFluidItems() {
        //Add FluidItems
        HashMap<Fluid, Item> fluidItemMap = new HashMap<>();

        BuiltInRegistries.FLUID.forEach(fluid -> {

            if (fluid == Fluids.EMPTY)
                return;
            if (!fluid.isSource(fluid.defaultFluidState()))
                return;

            ResourceLocation itemLocation = BuiltInRegistries.FLUID.getKey(fluid);
            Item item = Registry.register(
                    BuiltInRegistries.ITEM,
                    itemLocation,
                    new FluidItem(fluid.defaultFluidState().createLegacyBlock().getBlock(),
                            new FluidItem.FluidItemProperties()
                                    .fluid(fluid)
                                    .setItemId(ResourceKey.create(Registries.ITEM, itemLocation))
                    ));
            fluidItemMap.put(fluid, item);
        });

        ItemViewRecipes.INSTANCE.setFluidItemMap(fluidItemMap);
    }

}

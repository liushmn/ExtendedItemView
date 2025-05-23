package de.crafty.eiv.fabric.resolver;

import de.crafty.eiv.common.resolver.IEivClientResolver;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

public class FabricEivResolver implements IEivClientResolver {

    @Override
    public String getModNameForItem(Item item) {
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(BuiltInRegistries.ITEM.getKey(item).getNamespace());

        return optional.map(modContainer -> modContainer.getMetadata().getName()).orElse(null);
    }

    @Override
    public TextureAtlasSprite resolveFluidSprite(Fluid fluid) {
        return FluidRenderHandlerRegistryImpl.INSTANCE.get(fluid).getFluidSprites(null, null, fluid.defaultFluidState())[0];
    }
}

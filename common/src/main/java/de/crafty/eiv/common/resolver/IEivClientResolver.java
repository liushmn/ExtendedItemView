package de.crafty.eiv.common.resolver;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;


public interface IEivClientResolver {

    String getModNameForItem(Item item);

    TextureAtlasSprite resolveFluidSprite(Fluid fluid);

}

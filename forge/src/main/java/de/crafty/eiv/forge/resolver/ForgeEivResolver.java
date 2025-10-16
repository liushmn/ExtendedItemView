package de.crafty.eiv.forge.resolver;

import de.crafty.eiv.common.resolver.IEivClientResolver;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgeEivResolver implements IEivClientResolver {

    @Override
    public String getModNameForItem(Item item) {
        return FMLLoader.getLoadingModList().getMods().stream().filter(modInfo -> modInfo.getModId().equals(BuiltInRegistries.ITEM.getKey(item).getNamespace())).findFirst().get().getDisplayName();
    }

    @Override
    public UVInfo getUVInfo(TextureAtlasSprite sprite) {
        return new UVInfo(sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
    }

}

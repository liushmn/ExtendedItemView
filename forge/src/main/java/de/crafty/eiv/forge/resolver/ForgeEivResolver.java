package de.crafty.eiv.forge.resolver;

import com.mojang.blaze3d.platform.InputConstants;
import de.crafty.eiv.common.resolver.IEivClientResolver;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Map;

public class ForgeEivResolver implements IEivClientResolver {

    @Override
    public String getModNameForItem(Item item) {
        return FMLLoader.getLoadingModList().getMods().stream().filter(modInfo -> modInfo.getModId().equals(BuiltInRegistries.ITEM.getKey(item).getNamespace())).findFirst().get().getDisplayName();
    }

    @Override
    public UVInfo getUVInfo(TextureAtlasSprite sprite) {
        return new UVInfo(0, 1, 0, 1);
    }

    @Override
    public Map<InputConstants.Key, KeyMapping> getKeyMap() {
        return Map.of();
    }
}

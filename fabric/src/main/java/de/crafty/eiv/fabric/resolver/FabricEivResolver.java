package de.crafty.eiv.fabric.resolver;

import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.resolver.IEivClientResolver;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import java.util.Optional;

public class FabricEivResolver implements IEivClientResolver {

    @Override
    public String getModNameForItem(Item item) {
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(BuiltInRegistries.ITEM.getKey(item).getNamespace());

        return optional.map(modContainer -> modContainer.getMetadata().getName()).orElse("???");
    }


    @Override
    public UVInfo getUVInfo(TextureAtlasSprite sprite) {
        return new UVInfo(sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
    }

}

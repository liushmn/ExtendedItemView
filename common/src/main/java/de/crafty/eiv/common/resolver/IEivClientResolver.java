package de.crafty.eiv.common.resolver;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.Map;


public interface IEivClientResolver {

    String getModNameForItem(Item item);

    UVInfo getUVInfo(TextureAtlasSprite sprite);


    record UVInfo(float u0, float u1, float v0, float v1) {
    }

}

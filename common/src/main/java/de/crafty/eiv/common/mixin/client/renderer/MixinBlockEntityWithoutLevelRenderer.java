package de.crafty.eiv.common.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.extra.FluidStack;
import de.crafty.eiv.common.recipe.item.FluidItem;
import de.crafty.eiv.common.resolver.IEivClientResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public abstract class MixinBlockEntityWithoutLevelRenderer implements ResourceManagerReloadListener {


    @Inject(method = "renderByItem", at = @At("TAIL"))
    private void renderFluids(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay, CallbackInfo ci){
        if(!(itemStack.getItem() instanceof FluidItem fluidItem))
            return;

        FluidStack fluidStack = FluidStack.fromItemStack(itemStack);
        Fluid fluid = fluidStack.fluid();

        float renderHeight = Math.max(Math.min((float) fluidStack.amount() / (float) FluidStack.AMOUNT_FULL, 1.0F), 0.1F);



        int color = fluid == Fluids.WATER ? Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS).value().getWaterColor() : -1;
        Color unmodified = new Color(color);
        color = new Color(unmodified.getRed(), unmodified.getGreen(), unmodified.getBlue(), 255).getRGB();

        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(fluid.defaultFluidState().createLegacyBlock()).getParticleIcon();
        IEivClientResolver.UVInfo uvInfo = CommonEIVClient.resolver().getUVInfo(sprite);

        float u0 = uvInfo.u0();
        float u1 = uvInfo.u1();
        float v0 = uvInfo.v0();
        float v1 = uvInfo.v1();

        float width = (u1 - u0);
        float height = (v1 - v0);

        height *= renderHeight;

        poseStack.pushPose();
        poseStack.scale(1.0F, 1.0F, 1.0F);
        VertexConsumer vertexConsumer = sprite.wrap(ItemRenderer.getFoilBuffer(multiBufferSource, RenderType.entityTranslucent(sprite.atlasLocation()), itemDisplayContext == ItemDisplayContext.GUI, true));
        Matrix4f matrix4f = poseStack.last().pose();
        vertexConsumer.vertex(matrix4f, 0, 0, 0).uv(u0, v0).overlayCoords(overlay).uv2(light).color(color).normal(0.0F, 0.0F, 1.0F);
        vertexConsumer.vertex(matrix4f, 0, renderHeight, 0).uv(u0, v0 + height).overlayCoords(overlay).uv2(light).color(color).normal(0.0F, 0.0F, 1.0F);
        vertexConsumer.vertex(matrix4f, 1.0F, renderHeight, 0).uv(u0 + width, v0 + height).overlayCoords(overlay).uv2(light).color(color).normal(0.0F, 0.0F, 1.0F);
        vertexConsumer.vertex(matrix4f, 1.0F, 0, 0).uv(u0 + width, v0).overlayCoords(overlay).uv2(light).color(color).normal(0.0F, 0.0F, 1.0F);
        poseStack.popPose();
    }

}

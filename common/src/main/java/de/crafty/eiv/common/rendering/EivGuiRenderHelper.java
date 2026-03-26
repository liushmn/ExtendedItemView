package de.crafty.eiv.common.rendering;

import de.crafty.eiv.common.access.IEivEntity;
import de.crafty.eiv.common.access.IEivWrappedRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EivGuiRenderHelper {


    public static void renderEntityOnScreen(GuiGraphicsExtractor guiGraphicsExtractor, LivingEntity livingEntity, int x0, int y0, int x1, int y1, float scale, Vector3f translation, Quaternionf rotation, Quaternionf cameraAngleOverride) {

        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<LivingEntity, EntityRenderState> entityRenderer = (EntityRenderer<LivingEntity, EntityRenderState>) entityRenderDispatcher.getRenderer(livingEntity);

        EntityRenderState entityRenderState = entityRenderer.createRenderState();
        entityRenderer.extractRenderState(livingEntity, entityRenderState, 1.0F);
        IEivWrappedRenderState wrappedState = (IEivWrappedRenderState) entityRenderState;
        wrappedState.eiv$enableMultiRendering();

        entityRenderState.lightCoords = LightCoordsUtil.FULL_BRIGHT;

        wrappedState.eiv$setEmbeddingData(((IEivEntity) livingEntity).eiv$getEmbeddingData());

        guiGraphicsExtractor.entity(entityRenderState, scale, translation, rotation, cameraAngleOverride, x0, y0, x1, y1);
    }

}

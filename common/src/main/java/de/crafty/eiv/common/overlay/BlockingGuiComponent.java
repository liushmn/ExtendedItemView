package de.crafty.eiv.common.overlay;

import net.minecraft.resources.ResourceLocation;

import java.awt.*;

/**
 *
 * @param id     The id of the gui component (later used for in- and excluding the components in different overlays)
 * @param x      The current x-position in the screen
 * @param y      The current y-position in the screen
 * @param width  The current width of the component
 * @param height The current height of the component
 */
public record BlockingGuiComponent(ResourceLocation id, int x, int y, int width, int height) {


    public boolean hasIntersectionWith(int x, int y, int width, int height) {

        Rectangle rect = new Rectangle(this.x, this.y, this.width, this.height);
        Rectangle compared = new Rectangle(x, y, width, height);

        return rect.intersects(compared);
    }

}

package de.crafty.eiv.common.overlay;

import net.minecraft.resources.ResourceLocation;

/**
 *
 * @param id The id of the gui component (later used for in- and excluding the components in different overlays)
 * @param x The current x-position in the screen
 * @param y The current y-position in the screen
 * @param width The current width of the component
 * @param height The current height of the component
 */
public record BlockingGuiComponent(ResourceLocation id, int x, int y, int width, int height) {
}

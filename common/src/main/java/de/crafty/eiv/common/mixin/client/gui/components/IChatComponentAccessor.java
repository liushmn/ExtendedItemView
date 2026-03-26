package de.crafty.eiv.common.mixin.client.gui.components;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatComponent.class)
public interface IChatComponentAccessor {

    @Accessor(value = "allMessages", remap = false)
    List<GuiMessage> getAllMessages();

}

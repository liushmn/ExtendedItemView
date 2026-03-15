package de.crafty.eiv.common.mixin.client.gui.components;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatComponent.class)
public interface IChatComponentAccessor {

    @Accessor("allMessages")
    List<GuiMessage> getAllMessages();

}

package de.crafty.eiv.common.mixin.client;

import de.crafty.eiv.common.CommonEIV;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "close", at = @At("RETURN"))
    private void saveData(CallbackInfo ci) {
        CommonEIV.onExit();
    }

}

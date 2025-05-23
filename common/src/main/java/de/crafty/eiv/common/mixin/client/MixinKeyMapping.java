package de.crafty.eiv.common.mixin.client;

import de.crafty.eiv.common.CommonEIVClient;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public abstract class MixinKeyMapping {



    @Inject(method = "resetMapping", at = @At("RETURN"))
    private static void makeEivException(CallbackInfo ci){
        CommonEIVClient.excludeEivMappings();
    }
}

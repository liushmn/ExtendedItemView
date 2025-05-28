package de.crafty.eiv.fabric.mixin.client;

import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.fabric.FabricEIVClient;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public abstract class MixinKeyMapping {



    @Inject(method = "resetMapping", at = @At("RETURN"))
    private static void makeEivException(CallbackInfo ci){
        FabricEIVClient.excludeEivMappings();
    }
}

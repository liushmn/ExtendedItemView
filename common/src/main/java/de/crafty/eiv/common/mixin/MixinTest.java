package de.crafty.eiv.common.mixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.crafty.eiv.common.network.EivNetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(targets = "net/minecraft/network/protocol/common/custom/CustomPacketPayload$1")
public abstract class MixinTest {


    @Shadow protected abstract <B extends FriendlyByteBuf> StreamCodec<B, ? extends CustomPacketPayload> findCodec(ResourceLocation $$0x);


    @WrapOperation(method = {
            "writeCap",
            "decode(Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;"
    }, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload$1;findCodec(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/network/codec/StreamCodec;"))
    private <B extends FriendlyByteBuf> StreamCodec<? super B, ? extends CustomPacketPayload> overwriteCodec(@Coerce StreamCodec<B, CustomPacketPayload> instance, ResourceLocation $$0, Operation<StreamCodec<? super B, ? extends CustomPacketPayload>> original){

        if(EivNetworkManager.INSTANCE.getClientbound().containsKey($$0)){
            return (StreamCodec<? super B, ? extends CustomPacketPayload>) EivNetworkManager.INSTANCE.getClientbound().get($$0).codec();
        }

        if(EivNetworkManager.INSTANCE.getServerbound().containsKey($$0)){
            return (StreamCodec<? super B, ? extends CustomPacketPayload>) EivNetworkManager.INSTANCE.getServerbound().get($$0).codec();
        }

        return original.call(instance, $$0);
    }

}

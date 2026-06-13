package de.crafty.eiv.common.mixin.world.item.crafting;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.crafty.eiv.common.recipe.ClientRecipeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class MixinRecipeManager extends SimpleJsonResourceReloadListener {


    public MixinRecipeManager(Gson gson, String string) {
        super(gson, string);
    }

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("RETURN"))
    private void afterReload(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfo ci) {
        if(!((RecipeManager) (Object) this).equals(ClientRecipeManager.INSTANCE.getVanillaRecipeManager()))
            ClientRecipeManager.INSTANCE.setVanillaRecipeManager((RecipeManager) (Object) this);

        if(Minecraft.getInstance().level != null)
            ClientRecipeManager.INSTANCE.reload();
    }
}

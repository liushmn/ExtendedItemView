package de.crafty.eiv.fabric.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {


    @Accessor("MAP")
    static Map<InputConstants.Key, KeyMapping> getKeyMap(){
        throw new AssertionError();
    }

    @Accessor("ALL")
    static Map<String, KeyMapping> getAllKeyMap(){
        throw new AssertionError();
    }

}

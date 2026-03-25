package de.crafty.eiv.common.component;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.embeddings.EmbeddingData;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.UnaryOperator;

public class EivDataComponents {

    private static final HashMap<ResourceLocation, DataComponentType<?>> COMPONENTS = new HashMap<>();

    public static final DataComponentType<EmbeddingData> EMBEDDING_DATA = EivDataComponents.register("embedding_data", embeddingDataBuilder -> embeddingDataBuilder.persistent(EmbeddingData.CODEC));


    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
        ResourceLocation ResourceLocation = ResourceLocation.fromNamespaceAndPath(CommonEIV.MODID, id);
        DataComponentType<T> type = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation, builder.apply(DataComponentType.builder()).build());
        COMPONENTS.put(ResourceLocation, type);

        return type;
    }


    public static void logTypes() {
        CommonEIV.LOGGER.info("Present EIV DataComponent Types:");
        COMPONENTS.forEach((ResourceLocation, type) -> {
            CommonEIV.LOGGER.info("- {}", ResourceLocation.toString());
        });
    }

}

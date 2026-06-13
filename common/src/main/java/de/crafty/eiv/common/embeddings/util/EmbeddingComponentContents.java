package de.crafty.eiv.common.embeddings.util;

import net.minecraft.network.chat.ComponentContents;

import java.util.UUID;

public class EmbeddingComponentContents implements ComponentContents {


    private UUID embeddingId;

    private EmbeddingComponentContents() {
    }


    public void bindUUID(UUID embeddingId) {
        this.embeddingId = embeddingId;
    }


    public static EmbeddingComponentContents createUnbound() {
        return new EmbeddingComponentContents();
    }
}

package de.crafty.eiv.common.embeddings.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EmbeddingComponentContents implements PlainTextContents {


    private UUID embeddingId;

    private EmbeddingComponentContents() {
    }


    public void bindUUID(UUID embeddingId) {
        this.embeddingId = embeddingId;
    }

    @Override
    public @NotNull String text() {
        return "";
    }


    public static EmbeddingComponentContents createUnbound() {
        return new EmbeddingComponentContents();
    }
}

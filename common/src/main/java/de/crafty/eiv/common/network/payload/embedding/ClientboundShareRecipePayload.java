package de.crafty.eiv.common.network.payload.embedding;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.network.payload.ICustomEivPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class ClientboundShareRecipePayload implements ICustomEivPayload {


    private ResourceLocation recipeId;
    private CompoundTag extraData;
    private UUID senderUUID;

    public static final ResourceLocation ID = new ResourceLocation(CommonEIV.MODID, "share_recipe_to_client");


    public ClientboundShareRecipePayload(ResourceLocation recipeId, CompoundTag extraData, UUID uuid) {
        this.recipeId = recipeId;
        this.extraData = extraData;
        this.senderUUID = uuid;
    }

    public ClientboundShareRecipePayload(){
        this(null, null, null);
    }

    @Override
    public void writeTag(CompoundTag tag) {
        tag.putString("recipeId", this.recipeId.toString());
        tag.put("extraData", this.extraData);
        tag.putString("sender", this.senderUUID.toString());
    }

    @Override
    public void readTag(CompoundTag tag) {
        this.recipeId = ResourceLocation.tryParse(tag.getString("recipeId"));
        this.extraData = tag.getCompound("extraData");
        this.senderUUID = UUID.fromString(tag.getString("sender"));
    }

    @Override
    public ResourceLocation getIdentifier() {
        return ID;
    }

    public ResourceLocation getRecipeId() {
        return this.recipeId;
    }

    public CompoundTag getExtraData() {
        return this.extraData;
    }

    public UUID getSender() {
        return this.senderUUID;
    }
}

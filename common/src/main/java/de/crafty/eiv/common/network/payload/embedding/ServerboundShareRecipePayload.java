package de.crafty.eiv.common.network.payload.embedding;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.network.payload.ICustomEivPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ServerboundShareRecipePayload implements ICustomEivPayload {

    private ResourceLocation recipeId;
    private CompoundTag extraData;

    public static final ResourceLocation ID = new ResourceLocation(CommonEIV.MODID, "share_recipe_to_server");



    public ServerboundShareRecipePayload(ResourceLocation recipeId, CompoundTag extraData){
        this.recipeId = recipeId;
        this.extraData = extraData;
    }

    public ServerboundShareRecipePayload(){}


    @Override
    public void writeTag(CompoundTag tag) {
        tag.putString("recipeId", this.recipeId.toString());
        tag.put("extraData", this.extraData);
    }

    @Override
    public void readTag(CompoundTag tag) {
        this.recipeId = ResourceLocation.tryParse(tag.getString("recipeId"));
        this.extraData = tag.getCompound("extraData");
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
}

package de.crafty.eiv.servercompat.builtin.smithing;

import de.crafty.eiv.servercompat.api.recipe.EivCompatRecipeType;
import de.crafty.eiv.servercompat.api.recipe.IEivCompatServerRecipe;
import de.crafty.eiv.servercompat.util.EivCompatTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.TransmuteResult;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Nullable;

public class CompatSmithingRecipe implements IEivCompatServerRecipe {


    public static final EivCompatRecipeType<CompatSmithingRecipe> TYPE = EivCompatRecipeType.register(
            new ResourceLocation("smithing")
    );

    private boolean isTrim;
    private RecipeChoice base, template, addition;
    private TrimPattern pattern;

    private TransmuteResult upgradeResult;

    public CompatSmithingRecipe(boolean isTrim, RecipeChoice base, RecipeChoice template, RecipeChoice addition, TrimPattern pattern, @Nullable TransmuteResult upgradeResult) {
        this.isTrim = isTrim;
        this.base = base;
        this.template = template;
        this.addition = addition;

        this.pattern = pattern;
        this.upgradeResult = upgradeResult;
    }


    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putBoolean("isTrim", this.isTrim);
        tag.put("base", EivCompatTagUtil.writeChoice(this.base));
        tag.put("template", EivCompatTagUtil.writeChoice(this.template));
        tag.put("addition", EivCompatTagUtil.writeChoice(this.addition));

        if(this.pattern != null)
            tag.put("pattern", TrimPattern.DIRECT_CODEC.encode(this.pattern, NbtOps.INSTANCE, new CompoundTag()).getOrThrow());

        if(this.upgradeResult != null)
            tag.put("upgradeResult", TransmuteResult.CODEC.encode(this.upgradeResult, NbtOps.INSTANCE, new CompoundTag()).getOrThrow());
    }

    @Override
    public EivCompatRecipeType<? extends IEivCompatServerRecipe> getRecipeType() {
        return TYPE;
    }
}

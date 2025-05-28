package de.crafty.eiv.common.builtin.smithing;

import com.mojang.datafixers.util.Pair;
import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.builtin.shapeless.ShapelessServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.equipment.trim.TrimPattern;

import java.util.List;

public class SmithingServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<SmithingServerRecipe> TYPE = EivRecipeType.register(
            ResourceLocation.withDefaultNamespace("smithing"),
            () -> new SmithingServerRecipe(false, null, null, null, null)
    );

    private boolean isTrim;
    private Ingredient base, template, addition;
    private TrimPattern pattern;

    public SmithingServerRecipe(boolean isTrim, Ingredient base, Ingredient template, Ingredient addition, TrimPattern pattern) {
        this.isTrim = isTrim;
        this.base = base;
        this.template = template;
        this.addition = addition;

        this.pattern = pattern;
    }

    public boolean isTrim() {
        return this.isTrim;
    }

    public Ingredient getBase() {
        return this.base;
    }

    public Ingredient getTemplate() {
        return this.template;
    }

    public Ingredient getAddition() {
        return this.addition;
    }

    public TrimPattern getPattern() {
        return this.pattern;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putBoolean("isTrim", this.isTrim);
        tag.put("base", EivTagUtil.writeIngredient(this.base));
        tag.put("template", EivTagUtil.writeIngredient(this.template));
        tag.put("addition", EivTagUtil.writeIngredient(this.addition));

        if(this.pattern != null)
            tag.put("pattern", TrimPattern.DIRECT_CODEC.encode(this.pattern, NbtOps.INSTANCE, new CompoundTag()).getOrThrow());

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.isTrim = tag.getBooleanOr("isTrim", false);
        this.base = EivTagUtil.readIngredient(tag.getCompound("base").orElseGet(CompoundTag::new));
        this.template = EivTagUtil.readIngredient(tag.getCompound("template").orElseGet(CompoundTag::new));
        this.addition = EivTagUtil.readIngredient(tag.getCompound("addition").orElseGet(CompoundTag::new));

        this.pattern = TrimPattern.DIRECT_CODEC.decode(NbtOps.INSTANCE, tag.getCompound("pattern").orElseGet(CompoundTag::new)).mapOrElse(Pair::getFirst, pairError -> null);

    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}

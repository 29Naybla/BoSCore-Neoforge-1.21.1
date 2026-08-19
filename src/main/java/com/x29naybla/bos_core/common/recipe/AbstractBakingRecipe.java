package com.x29naybla.bos_core.common.recipe;

import com.x29naybla.bos_core.common.block.entity.OvenBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBakingRecipe implements Recipe<OvenBlockEntity.SingleRecipeInputContainer> {
    protected final String group;
    protected final BakingBookCategory category;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final int cookTime;

    public AbstractBakingRecipe(String group, BakingBookCategory category, ItemStack output, NonNullList<Ingredient> recipeItems, int cookTime) {
        this.group = group;
        this.category = category;
        this.output = output;
        this.recipeItems = recipeItems;
        this.cookTime = cookTime;
    }

    @Override
    public @NotNull String getGroup() {
        return this.group;
    }

    public BakingBookCategory getCategory() {
        return this.category;
    }

    public int getCookTime() {
        return this.cookTime;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public boolean fuelMatches(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.getBurnTime(RecipeType.SMOKING) > 0 || AbstractFurnaceBlockEntity.isFuel(stack);
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return output.copy();
    }

    public ItemStack getOutput() {
        return output;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }
}

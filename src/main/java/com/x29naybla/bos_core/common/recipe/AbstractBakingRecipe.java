package com.x29naybla.bos_core.common.recipe;

import com.x29naybla.bos_core.client.recipe_book.BakingBookCategory;
import com.x29naybla.bos_core.common.registry.BoSBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBakingRecipe implements Recipe<RecipeWrapper> {
    protected final String group;
    protected final BakingBookCategory category;
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack output;
    private final float experience;
    private final int cookTime;

    public AbstractBakingRecipe(String group, @Nullable BakingBookCategory category, NonNullList<Ingredient> recipeItems, ItemStack output, float experience, int cookTime) {
        this.group = group;
        this.category = category;
        this.recipeItems = recipeItems;
        this.output = output;
        this.experience = experience;
        this.cookTime = cookTime;
    }

    @Override
    public @NotNull String getGroup() {
        return this.group;
    }

    @Nullable
    public BakingBookCategory getCategory() {
        return this.category;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return this.output;
    }

    public float getExperience() {
        return this.experience;
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
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(BoSBlocks.OVEN.get());
    }
}

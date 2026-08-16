package com.x29naybla.bos_core.common.recipe;

import com.x29naybla.bos_core.common.block.entity.OvenBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AbstractBakingRecipe implements Recipe<OvenBlockEntity.SingleRecipeInputContainer> {
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Ingredient>> FUEL_STREAM_CODEC =
            ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC);

    protected final String group;
    protected final BakingBookCategory category;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final Optional<Ingredient> fuel;
    private final int cookTime;

    public AbstractBakingRecipe(String group, BakingBookCategory category, ItemStack output, NonNullList<Ingredient> recipeItems,  Optional<Ingredient> fuel, int cookTime) {
        this.group = group;
        this.category = category;
        this.output = output;
        this.recipeItems = recipeItems;
        this.fuel = fuel;
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

    public Optional<Ingredient> getFuel() {
        return this.fuel;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public boolean fuelMatches(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return this.fuel.map(ingredient ->
                ingredient.test(stack)).orElseGet(() ->
                stack.getBurnTime(RecipeType.SMOKING) > 0 || AbstractFurnaceBlockEntity.isFuel(stack));
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

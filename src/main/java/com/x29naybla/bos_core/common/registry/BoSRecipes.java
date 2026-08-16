package com.x29naybla.bos_core.common.registry;

import com.x29naybla.bos_core.BoSCore;
import com.x29naybla.bos_core.common.recipe.AbstractBakingRecipe;
import com.x29naybla.bos_core.common.recipe.BakingShapedRecipe;
import com.x29naybla.bos_core.common.recipe.BakingShapelessRecipe;
import com.x29naybla.bos_core.common.recipe.DyeNameTagRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BoSRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, BoSCore.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, BoSCore.MODID);

    public static final Supplier<SimpleCraftingRecipeSerializer<?>> DYE_NAME_TAG =
            RECIPE_SERIALIZERS.register("dye_name_tag", () -> new SimpleCraftingRecipeSerializer<>(DyeNameTagRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<AbstractBakingRecipe>> BAKING_TYPE =
            RECIPE_TYPES.register("baking", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "baking";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BakingShapelessRecipe>> BAKING_SHAPELESS_SERIALIZER =
            RECIPE_SERIALIZERS.register("baking_shapeless", () -> BakingShapelessRecipe.Serializer.INSTANCE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BakingShapedRecipe>> BAKING_SHAPED_SERIALIZER =
            RECIPE_SERIALIZERS.register("baking_shaped", () -> BakingShapedRecipe.Serializer.INSTANCE);

}

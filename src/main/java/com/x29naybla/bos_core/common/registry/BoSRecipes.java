package com.x29naybla.bos_core.common.registry;

import com.x29naybla.bos_core.BoSCore;
import com.x29naybla.bos_core.common.recipe.DyeNameTagRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BoSRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, BoSCore.MODID);

    public static final Supplier<SimpleCraftingRecipeSerializer<?>> DYE_NAME_TAG =
            RECIPE_SERIALIZERS.register("dye_name_tag", () -> new SimpleCraftingRecipeSerializer<>(DyeNameTagRecipe::new));
}

package com.x29naybla.bos_core.client.recipe_book;

import com.google.common.collect.ImmutableList;
import com.x29naybla.bos_core.common.recipe.AbstractBakingRecipe;
import com.x29naybla.bos_core.common.registry.BoSRecipeTypes;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;

public class RecipeCategories {
    public static RecipeBookCategories BAKING_SEARCH = RecipeBookCategories.valueOf("BOS_CORE_BAKING_SEARCH");
    public static RecipeBookCategories BAKING_FOOD = RecipeBookCategories.valueOf("BOS_CORE_BAKING_FOOD");
    public static RecipeBookCategories BAKING_METALLURGY = RecipeBookCategories.valueOf("BOS_CORE_BAKING_METALLURGY");
    public static RecipeBookCategories BAKING_POTTERY = RecipeBookCategories.valueOf("BOS_CORE_BAKING_POTTERY");
    public static RecipeBookCategories BAKING_MISC = RecipeBookCategories.valueOf("BOS_CORE_BAKING_MISC");

    public static void init(RegisterRecipeBookCategoriesEvent event) {
        event.registerBookCategories(RecipeBookType.valueOf("BOS_CORE_BAKING"), ImmutableList.of(BAKING_SEARCH, BAKING_FOOD, BAKING_METALLURGY, BAKING_POTTERY, BAKING_MISC));
        event.registerAggregateCategory(BAKING_SEARCH, ImmutableList.of(BAKING_FOOD, BAKING_METALLURGY, BAKING_POTTERY, BAKING_MISC));
        event.registerRecipeCategoryFinder(BoSRecipeTypes.BAKING.get(), recipe ->
        {
            if (recipe.value() instanceof AbstractBakingRecipe bakingRecipe) {
                BakingBookCategory tab = bakingRecipe.getCategory();
                if (tab != null) {
                    return switch (tab) {
                        case FOOD -> BAKING_FOOD;
                        case METALLURGY -> BAKING_METALLURGY;
                        case POTTERY -> BAKING_POTTERY;
                        case MISC -> BAKING_MISC;
                    };
                }
            }
            return BAKING_MISC;
        });
    }
}

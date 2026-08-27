package com.x29naybla.bos_core.client.gui;

import com.x29naybla.bos_core.BoSCore;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class OvenRecipeBookComponent extends RecipeBookComponent {
    protected static final WidgetSprites RECIPE_BOOK_BUTTONS = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(BoSCore.MODID, "recipe_book/oven_enabled"),
            ResourceLocation.fromNamespaceAndPath(BoSCore.MODID, "recipe_book/oven_disabled"),
            ResourceLocation.fromNamespaceAndPath(BoSCore.MODID, "recipe_book/oven_enabled_highlighted"),
            ResourceLocation.fromNamespaceAndPath(BoSCore.MODID, "recipe_book/oven_disabled_highlighted"));

    @Override
    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(RECIPE_BOOK_BUTTONS);
    }

    @Override
    @Nonnull
    protected Component getRecipeFilterName() {
        return Component.translatable("recipe_book.bakeable");
    }

}

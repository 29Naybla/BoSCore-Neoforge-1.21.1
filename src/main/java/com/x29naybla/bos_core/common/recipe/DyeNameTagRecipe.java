package com.x29naybla.bos_core.common.recipe;

import com.x29naybla.bos_core.common.registry.BoSRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DyeNameTagRecipe extends CustomRecipe {
    public DyeNameTagRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, @NotNull Level level) {
        ItemStack nameTag = ItemStack.EMPTY;
        ItemStack dyeItem = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack selectedStack = input.getItem(i);
            if (!selectedStack.isEmpty()) {
                if (selectedStack.is(Items.NAME_TAG)){
                    if (!nameTag.isEmpty()) {
                        return false;
                    }
                    nameTag = selectedStack;
                } else if (selectedStack.getItem() instanceof DyeItem) {
                    if (!dyeItem.isEmpty()) {
                        return false;
                    }
                    dyeItem = selectedStack;
                }
            }
        }

        return !nameTag.isEmpty() && !dyeItem.isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput input, HolderLookup.@NotNull Provider registries) {
        ItemStack nameTag = ItemStack.EMPTY;
        ItemStack dyeItem = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack selectedStack = input.getItem(i);
            if (!selectedStack.isEmpty()) {
                if (selectedStack.is(Items.NAME_TAG)) {
                    if (!nameTag.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    nameTag = selectedStack.copyWithCount(1);
                } else {
                    if (!(selectedStack.getItem() instanceof DyeItem seletctedDyeItem)) {
                        return ItemStack.EMPTY;
                    }
                    dyeItem = seletctedDyeItem.getDefaultInstance();
                }
            }
        }

        return !nameTag.isEmpty() && !dyeItem.isEmpty() ? dyeNameTag(nameTag, dyeItem) : ItemStack.EMPTY;
    }

    private ItemStack dyeNameTag(ItemStack nameTag, ItemStack dyeItem){
        int color = ((DyeItem) dyeItem.getItem()).getDyeColor().getTextureDiffuseColor();

        nameTag.set(DataComponents.DYED_COLOR,  new DyedItemColor(color, true));

        return nameTag;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return BoSRecipeSerializers.DYE_NAME_TAG.get();
    }
}

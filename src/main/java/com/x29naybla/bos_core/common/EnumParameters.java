package com.x29naybla.bos_core.common;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.List;
import java.util.function.Supplier;

public class EnumParameters {
    public static final EnumProxy<RecipeBookCategories> PROXY_BAKING_SEARCH = new EnumProxy<>(
            RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(new ItemStack(Items.COMPASS))
    );
    public static final EnumProxy<RecipeBookCategories> PROXY_BAKING_FOOD = new EnumProxy<>(
            RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(new ItemStack(Items.BREAD))
    );
    public static final EnumProxy<RecipeBookCategories> PROXY_BAKING_METALLURGY = new EnumProxy<>(
            RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(new ItemStack(Items.COPPER_INGOT))
    );
    public static final EnumProxy<RecipeBookCategories> PROXY_BAKING_POTTERY = new EnumProxy<>(
            RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(new ItemStack(Items.BRICK))
    );
    public static final EnumProxy<RecipeBookCategories> PROXY_BAKING_MISC = new EnumProxy<>(
            RecipeBookCategories.class, (Supplier<List<ItemStack>>) () -> List.of(new ItemStack(Items.SUGAR))
    );
}

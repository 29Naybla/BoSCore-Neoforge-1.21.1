package com.x29naybla.bos_core.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.x29naybla.bos_core.common.registry.BoSRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

public class BakingShapelessRecipe extends AbstractBakingRecipe {
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack output;
    private final int cookTime;

    public BakingShapelessRecipe(String group, BakingBookCategory category, ItemStack output, NonNullList<Ingredient> recipeItems, int cookTime) {
        super(group, category, output, recipeItems, cookTime);
        this.output = output;
        this.recipeItems = recipeItems;
        this.cookTime = cookTime;
    }

    @Override
    public boolean matches(@NotNull RecipeWrapper input, @NotNull Level level) {
        int ingredientCount = 0;
        for (int j = 0; j < 9; ++j) {
            ItemStack stack = input.getItem(j);
            if (!stack.isEmpty()) {
                ingredientCount++;
            }
        }
        if (ingredientCount != this.recipeItems.size()) {
            return false;
        }

        boolean[] used = new boolean[9];
        for (Ingredient ingredient : recipeItems) {
            boolean matched = false;
            for (int j = 0; j < 9; ++j) {
                if (!used[j] && ingredient.test(input.getItem(j))) {
                    used[j] = true;
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeWrapper input, HolderLookup.@NotNull Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return BoSRecipes.BAKING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BakingShapelessRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private static final MapCodec<BakingShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(BakingShapelessRecipe::getGroup),
                BakingBookCategory.CODEC.optionalFieldOf("category", BakingBookCategory.MISC).forGetter(BakingShapelessRecipe::getCategory),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.recipeItems),
                Codec.INT.optionalFieldOf("cookingTime", 200).forGetter(BakingShapelessRecipe::getCookTime)
        ).apply(instance, (group, category, result, ingredients, cookTime) ->
                new BakingShapelessRecipe(group, category, result, NonNullList.copyOf(ingredients), cookTime)));

        private static final StreamCodec<RegistryFriendlyByteBuf, BakingShapelessRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(recipe.group);
                    buf.writeEnum(recipe.category);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                    buf.writeVarInt(recipe.recipeItems.size());
                    for (Ingredient ing : recipe.recipeItems) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
                    }
                    buf.writeVarInt(recipe.cookTime);
                },
                buf -> {
                    String group = buf.readUtf();
                    BakingBookCategory category = buf.readEnum(BakingBookCategory.class);
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                    int size = buf.readVarInt();
                    NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
                    for (int i = 0; i < size; i++) {
                        ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                    }
                    int cookTime = buf.readVarInt();
                    return new BakingShapelessRecipe(group, category, output, ingredients, cookTime);
                }
        );

        @Override
        public @NotNull MapCodec<BakingShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, BakingShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

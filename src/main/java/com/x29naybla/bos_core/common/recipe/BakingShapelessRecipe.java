package com.x29naybla.bos_core.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.x29naybla.bos_core.client.recipe_book.BakingBookCategory;
import com.x29naybla.bos_core.common.registry.BoSRecipeTypes;
import com.x29naybla.bos_core.common.registry.BoSRecipeSerializers;
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
    private final float experience;
    private final int cookTime;

    public BakingShapelessRecipe(String group, BakingBookCategory category, ItemStack output, NonNullList<Ingredient> recipeItems, float experience, int cookTime) {
        super(group, category, recipeItems, output, experience, cookTime);
        this.output = output;
        this.recipeItems = recipeItems;
        this.experience = experience;
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
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return BoSRecipeSerializers.BAKING_SHAPELESS.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return BoSRecipeTypes.BAKING.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BakingShapelessRecipe that = (BakingShapelessRecipe) obj;

        if (Float.compare(that.getExperience(), getExperience()) != 0) return false;
        if (getCookTime() != that.getCookTime()) return false;
        if (!getGroup().equals(that.getGroup())) return false;
        if (category != that.category) return false;
        if (!recipeItems.equals(that.getIngredients())) return false;
        return output.equals(that.output);
    }

    @Override
    public int hashCode() {
        int result = getGroup().hashCode();
        result = 31 * result + (getCategory() != null ? getCategory().hashCode() : 0);
        result = 31 * result + output.hashCode();
        result = 31 * result + recipeItems.hashCode();
        result = 31 * result + (getExperience() != 0.0f ? Float.floatToIntBits(getExperience()) : 0);
        result = 31 * result + getCookTime();
        return result;
    }

    public static class Serializer implements RecipeSerializer<BakingShapelessRecipe> {
        private static final MapCodec<BakingShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(BakingShapelessRecipe::getGroup),
                BakingBookCategory.CODEC.optionalFieldOf("category", BakingBookCategory.MISC).forGetter(BakingShapelessRecipe::getCategory),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.recipeItems),
                Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(BakingShapelessRecipe::getExperience),
                Codec.INT.optionalFieldOf("cookingTime", 200).forGetter(BakingShapelessRecipe::getCookTime)
        ).apply(instance, (group, category, result, ingredients, experience, cookTime) ->
                new BakingShapelessRecipe(group, category, result, NonNullList.copyOf(ingredients), experience, cookTime)));

        public static final StreamCodec<RegistryFriendlyByteBuf, BakingShapelessRecipe> STREAM_CODEC = StreamCodec.of(BakingShapelessRecipe.Serializer::toNetwork, BakingShapelessRecipe.Serializer::fromNetwork);

        public Serializer() {

        }

        @Override
        public @NotNull MapCodec<BakingShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, BakingShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static BakingShapelessRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            BakingBookCategory category =  BakingBookCategory.findByName(buf.readUtf());
            ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
            int size = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) {
                ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            }
            float experience = buf.readFloat();
            int cookTime = buf.readVarInt();
            return new BakingShapelessRecipe(group, category, output, ingredients, experience, cookTime);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, BakingShapelessRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeUtf(recipe.category != null ? recipe.category.toString() : "");
            ItemStack.STREAM_CODEC.encode(buf, recipe.output);
            buf.writeVarInt(recipe.recipeItems.size());
            for (Ingredient ing : recipe.recipeItems) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
            }
            buf.writeFloat(recipe.experience);
            buf.writeVarInt(recipe.cookTime);
        }
    }
}

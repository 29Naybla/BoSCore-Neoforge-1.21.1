package com.x29naybla.bos_core.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.x29naybla.bos_core.client.recipe_book.BakingBookCategory;
import com.x29naybla.bos_core.common.registry.BoSRecipeTypes;
import com.x29naybla.bos_core.common.registry.BoSRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

public class BakingShapedRecipe extends AbstractBakingRecipe {
    private final ShapedRecipePattern pattern;
    final ItemStack output;
    private final float experience;
    private final int cookTime;

    public BakingShapedRecipe(String group, BakingBookCategory category, ShapedRecipePattern pattern, ItemStack output, float experience, int cookTime) {
        super(group, category, pattern.ingredients(), output, experience, cookTime);
        this.pattern = pattern;
        this.output = output;
        this.experience = experience;
        this.cookTime = cookTime;
    }

    @Override
    public boolean matches(@NotNull RecipeWrapper input, @NotNull Level level) {
        for (int xOffset = 0; xOffset <= 3 - this.getWidth(); ++xOffset) {
            for (int yOffset = 0; yOffset <= 3 - this.getHeight(); ++yOffset) {
                if (this.matches(input, xOffset, yOffset, true)) return true;
                if (this.matches(input, xOffset, yOffset, false)) return true;
            }
        }
        return false;
    }

    private boolean matches(RecipeWrapper input, int xOffset, int yOffset, boolean mirrored) {
        int width = this.getWidth();
        int height = this.getHeight();
        for (int xn = 0; xn < 3; ++xn) {
            for (int yn = 0; yn < 3; ++yn) {
                int x = xn - xOffset;
                int y = yn - yOffset;
                Ingredient ingredient = Ingredient.EMPTY;
                if (x >= 0 && y >= 0 && x < width && y < height) {
                    if (mirrored) ingredient = this.getIngredients().get(width - x - 1 + y * width);
                    else ingredient = this.getIngredients().get(x + y * width);
                }
                if (!ingredient.test(input.getItem(xn + yn * 3))) return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeWrapper input, HolderLookup.@NotNull Provider registries) {
        return this.output.copy();
    }

    public ShapedRecipePattern getPattern() {
        return this.pattern;
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return BoSRecipeSerializers.BAKING_SHAPED.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return BoSRecipeTypes.BAKING.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BakingShapedRecipe that = (BakingShapedRecipe) obj;

        if (Float.compare(that.getExperience(), getExperience()) != 0) return false;
        if (getCookTime() != that.getCookTime()) return false;
        if (!getGroup().equals(that.getGroup())) return false;
        if (category != that.category) return false;
        if (!pattern.equals(that.pattern)) return false;
        return output.equals(that.output);
    }

    @Override
    public int hashCode() {
        int result = getGroup().hashCode();
        result = 31 * result + (getCategory() != null ? getCategory().hashCode() : 0);
        result = 31 * result + output.hashCode();
        result = 31 * result + pattern.hashCode();
        result = 31 * result + (getExperience() != 0.0f ? Float.floatToIntBits(getExperience()) : 0);
        result = 31 * result + getCookTime();
        return result;
    }

    public static class Serializer implements RecipeSerializer<BakingShapedRecipe> {
        private static final MapCodec<BakingShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(BakingShapedRecipe::getGroup),
                BakingBookCategory.CODEC.optionalFieldOf("category", BakingBookCategory.MISC).forGetter(BakingShapedRecipe::getCategory),
                ShapedRecipePattern.MAP_CODEC.forGetter(BakingShapedRecipe::getPattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(BakingShapedRecipe::getExperience),
                Codec.INT.optionalFieldOf("cookingTime", 200).forGetter(BakingShapedRecipe::getCookTime)
        ).apply(instance, BakingShapedRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BakingShapedRecipe> STREAM_CODEC = StreamCodec.of(BakingShapedRecipe.Serializer::toNetwork, BakingShapedRecipe.Serializer::fromNetwork);

        public Serializer() {

        }

        @Override
        public @NotNull MapCodec<BakingShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, BakingShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static BakingShapedRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            BakingBookCategory category = BakingBookCategory.findByName(buf.readUtf());
            ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
            ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
            float experience = buf.readFloat();
            int cookTime = buf.readVarInt();
            return new BakingShapedRecipe(group, category, pattern, output, experience, cookTime);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, BakingShapedRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeUtf(recipe.category != null ? recipe.category.toString() : "");
            ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buf, recipe.output);
            buf.writeFloat(recipe.experience);
            buf.writeVarInt(recipe.cookTime);
        }
    }
}

package com.x29naybla.bos_core.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.x29naybla.bos_core.common.block.entity.OvenBlockEntity;
import com.x29naybla.bos_core.common.registry.BoSRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BakingShapedRecipe extends AbstractBakingRecipe {
    private final ShapedRecipePattern pattern;
    private final ItemStack output;
    private final int cookTime;

    public BakingShapedRecipe(String group, BakingBookCategory category, ShapedRecipePattern pattern, ItemStack output, int cookTime) {
        super(group, category, output, pattern.ingredients(), cookTime);
        this.pattern = pattern;
        this.output = output;
        this.cookTime = cookTime;
    }

    @Override
    public boolean matches(OvenBlockEntity.@NotNull SingleRecipeInputContainer input, @NotNull Level level) {
        for (int xOffset = 0; xOffset <= 3 - this.getWidth(); ++xOffset) {
            for (int yOffset = 0; yOffset <= 3 - this.getHeight(); ++yOffset) {
                if (this.matches(input, xOffset, yOffset, true)) return true;
                if (this.matches(input, xOffset, yOffset, false)) return true;
            }
        }
        return false;
    }

    private boolean matches(OvenBlockEntity.SingleRecipeInputContainer input, int xOffset, int yOffset, boolean mirrored) {
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
    public @NotNull ItemStack assemble(OvenBlockEntity.@NotNull SingleRecipeInputContainer input, HolderLookup.@NotNull Provider registries) {
        return output.copy();
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
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return BoSRecipes.BAKING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BakingShapedRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private static final MapCodec<BakingShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(BakingShapedRecipe::getGroup),
                BakingBookCategory.CODEC.optionalFieldOf("category", BakingBookCategory.MISC).forGetter(BakingShapedRecipe::getCategory),
                ShapedRecipePattern.MAP_CODEC.forGetter(BakingShapedRecipe::getPattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                Codec.INT.optionalFieldOf("cookingTime", 200).forGetter(BakingShapedRecipe::getCookTime)
        ).apply(instance, BakingShapedRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, BakingShapedRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(recipe.group);
                    buf.writeEnum(recipe.category);
                    ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.pattern);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                    buf.writeVarInt(recipe.cookTime);
                },
                buf -> {
                    String group = buf.readUtf();
                    BakingBookCategory category = buf.readEnum(BakingBookCategory.class);
                    ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                    int cookTime = buf.readVarInt();
                    return new BakingShapedRecipe(group, category, pattern, output, cookTime);
                }
        );

        @Override
        public @NotNull MapCodec<BakingShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, BakingShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

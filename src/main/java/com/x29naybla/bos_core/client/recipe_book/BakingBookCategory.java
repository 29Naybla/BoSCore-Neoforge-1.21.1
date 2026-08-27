package com.x29naybla.bos_core.client.recipe_book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public enum BakingBookCategory implements StringRepresentable {
    FOOD("food"),
    METALLURGY("metallurgy"),
    POTTERY("pottery"),
    MISC("misc");

    public static final Codec<BakingBookCategory> CODEC = Codec.STRING.flatXmap(s -> {
        BakingBookCategory category = findByName(s);
        if (category == null) {
            return DataResult.error(() -> "Optional field 'recipe_book_tab' does not match any valid tab. If defined, must be one of the following: " + EnumSet.allOf(BakingBookCategory.class));
        }
        return DataResult.success(category);
    }, category -> DataResult.success(category.toString()));

    public final String name;

    BakingBookCategory(String name) {
        this.name = name;
    }

    public static BakingBookCategory findByName(String name) {
        for (BakingBookCategory value : values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}

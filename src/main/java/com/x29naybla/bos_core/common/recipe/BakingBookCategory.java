package com.x29naybla.bos_core.common.recipe;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum BakingBookCategory implements StringRepresentable {

    FOOD("food"),
    MISC("misc");

    public static final StringRepresentable.EnumCodec<BakingBookCategory> CODEC = StringRepresentable.fromEnum(BakingBookCategory::values);

    private final String name;

    BakingBookCategory(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}

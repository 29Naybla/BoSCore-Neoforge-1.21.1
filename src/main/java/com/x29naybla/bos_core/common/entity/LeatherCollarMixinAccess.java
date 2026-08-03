package com.x29naybla.bos_core.common.entity;

import net.minecraft.world.item.DyeColor;

public interface LeatherCollarMixinAccess {
    int bos_getLeatherCollarColor();

    void bos_addToLeatherCollarColor(DyeColor dyeColor);

    void bos_setLeatherCollarColor(int color);
}

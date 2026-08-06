package com.x29naybla.bos_core.common.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class PumpkinPieBlock extends PieBlock {

    public PumpkinPieBlock(Properties properties) {
        super(properties);
        this.FOOD = 7;
        this.SATURATION = 0.5F;
    }

    @Override
    public @NotNull Item asItem() {
        return Items.PUMPKIN_PIE;
    }
}

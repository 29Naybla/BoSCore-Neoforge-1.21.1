package com.x29naybla.bos_core.mixin;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {
    @Shadow
    private final DataSlot cost = DataSlot.standalone();


    /**
     * @author 29Naybla
     * @reason No xp cost
     */
    @Overwrite
    public static int calculateIncreasedRepairCost(int oldRepairCost) {
        return 0;
    }

    /**
     * @author 29Naybla
     * @reason No xp cost
     */
    @Overwrite
    public int getCost() {
        return 0;
    }

    /**
     * @author 29Naybla
     * @reason No xp cost
     */
    @Overwrite
    public void setMaximumCost(long value) {
        this.cost.set(0);
    }
}

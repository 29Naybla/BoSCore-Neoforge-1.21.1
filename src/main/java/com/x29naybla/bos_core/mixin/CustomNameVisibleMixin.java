package com.x29naybla.bos_core.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class CustomNameVisibleMixin {

    @Shadow
    public abstract boolean hasCustomName();

    /**
     * @author 29Naybla
     * @reason custom name visible only when looking at entity bad
     */
    @Overwrite
    public boolean isCustomNameVisible() {
        return this.hasCustomName();
    }
}

package com.x29naybla.bos_core.mixin;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(DimensionSpecialEffects.class)
public class SkyMixin {
    @Shadow
    private final float[] sunriseCol = new float[4];

    /**
     * @author 29Naybla
     * @reason Pink sunrise!! yay!! :D
     */
    @Nullable
    @Overwrite
    public float[] getSunriseColor(float timeOfDay, float partialTicks) {
        float f1 = Mth.cos(timeOfDay * (float) (Math.PI * 2)) - 0.0F;
        if (f1 >= -0.4F && f1 <= 0.4F) {
            float f3 = f1 / 0.4F * 0.5F + 0.5F;
            float f4 = 1.0F - (1.0F - Mth.sin(f3 * (float) Math.PI)) * 0.99F;
            f4 *= f4;
            this.sunriseCol[0] = f3 * 0.3F + 0.7F;
            this.sunriseCol[1] = f3 * f3 * 0.5F + 0.2F;
            this.sunriseCol[2] = f3 * f3 * 0.7F + 0.2F;
            this.sunriseCol[3] = f4;
            return this.sunriseCol;
        } else {
            return null;
        }
    }
}

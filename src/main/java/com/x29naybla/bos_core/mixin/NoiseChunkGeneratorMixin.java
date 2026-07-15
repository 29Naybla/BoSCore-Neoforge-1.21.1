package com.x29naybla.bos_core.mixin;

import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseChunkGeneratorMixin {

    /**
     * @author 29Naybla
     * @reason No more hardcoded -54 lava
     */
    @Overwrite
    private static Aquifer.FluidPicker createFluidPicker(NoiseGeneratorSettings settings) {
        return (x, y, z) -> new Aquifer.FluidStatus(settings.seaLevel(), settings.defaultFluid());
    }
}

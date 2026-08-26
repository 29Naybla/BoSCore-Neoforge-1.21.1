package com.x29naybla.bos_core.mixin;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = BedBlock.class, priority = 2000)
public abstract class BedBlockMixin extends Block {
    public BedBlockMixin(Properties properties) {
        super(properties);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        if(state.is(BlockTags.BEDS)) return RenderShape.MODEL;
        return super.getRenderShape(state);
    }
}

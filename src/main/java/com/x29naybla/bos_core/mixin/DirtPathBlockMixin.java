package com.x29naybla.bos_core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DirtPathBlock.class)
public class DirtPathBlockMixin {
    @Unique
    private static final VoxelShape BOS_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

    /**
     * @author 29Naybla
     * @reason Making paths be 2 pixels deeper instead of 1
     */
    @Overwrite
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return BOS_SHAPE;
    }
}

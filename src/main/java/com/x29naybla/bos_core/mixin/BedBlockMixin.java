package com.x29naybla.bos_core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

@Mixin(value = BedBlock.class, priority = 2000)
public abstract class BedBlockMixin extends Block {
    @Shadow
    @Final
    public static BooleanProperty OCCUPIED;
    @Unique
    private static final VoxelShape BOS_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);
    @Unique
    private static final VoxelShape BOS_SHAPE_OCCUPIED = Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

    public BedBlockMixin(Properties properties) {
        super(properties);
    }

    /**
     * @author 29Naybla
     * @reason Changing the shape to be simpler and the appropriate size when it's occupied
     */
    @Overwrite
    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if(state.getValue(OCCUPIED)) return BOS_SHAPE_OCCUPIED;
        else return BOS_SHAPE;
    }

    /**
     * @author 29Naybla
     * @reason giving beds a block renderer
     */
    @Overwrite
    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        if(state.is(BlockTags.BEDS)) return RenderShape.MODEL;
        return super.getRenderShape(state);
    }
}

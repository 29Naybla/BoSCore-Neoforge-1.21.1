package com.x29naybla.bos_core.common.block;

import com.x29naybla.bos_core.common.registry.BoSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class AppleFlowersBlock extends CropBlock {
    public static final int MAX_AGE = 10;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 10);
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(7.0, 12.0, 7.0, 9.0, 16.0, 9.0),
            Block.box(5.0, 12.0, 5.0, 11.0, 16.0, 11.0),
            Block.box(5.0, 9.0, 5.0, 11.0, 16.0, 11.0),
            Block.box(4.0, 8.0, 5.0, 12.0, 16.0, 11.0),
            Block.box(4.0, 7.0, 4.0, 12.0, 16.0, 12.0),
            Block.box(4.0, 7.0, 4.0, 12.0, 16.0, 12.0),
            Block.box(4.0, 7.0, 4.0, 12.0, 16.0, 12.0),
            Block.box(3.0, 6.0, 3.0, 13.0, 16.0, 13.0),
            Block.box(3.0, 6.0, 3.0, 13.0, 16.0, 13.0),
            Block.box(3.0, 6.0, 3.0, 13.0, 16.0, 13.0),
            Block.box(2.0, 3.0, 2.0, 14.0, 16.0, 14.0)
    };

    public AppleFlowersBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE_BY_AGE[this.getAge(state)];
    }

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.is(BoSBlocks.FLOWERING_APPLE_LEAVES);
    }

    @Override
    protected @NotNull IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.above()).is(BoSBlocks.FLOWERING_APPLE_LEAVES);
    }

    @Override
    protected void randomTick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (level.getRawBrightness(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                float f = 6.0F;
                if (net.neoforged.neoforge.common.CommonHooks.canCropGrow(level, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
                    level.setBlock(pos, this.getStateForAge(i + 1), 2);
                    net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(level, pos, state);
                }
            }
        }
    }

    @Override
    protected @NotNull ItemLike getBaseSeedId() {
        return Items.APPLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}

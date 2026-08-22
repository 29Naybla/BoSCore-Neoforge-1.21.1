package com.x29naybla.bos_core.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class FruitingLeavesBlock extends LeavesFlammableBlock{
    private final Block fruitCropBlock;

    public FruitingLeavesBlock(Properties properties, Block fruitCropBlock) {
        super(properties);
        this.fruitCropBlock = fruitCropBlock;
    }

    @Override
    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (fruitsCount(level, pos) && level.getBlockState(pos.below()).isEmpty()) {
            level.setBlock(pos.below(), fruitCropBlock.defaultBlockState(), 2);
        }
        super.randomTick(state, level, pos, random);
    }

    private boolean fruitsCount(ServerLevel level, BlockPos pos) {
        int fruits = 0;
        for(int x = -2; x <= 2; x++) {
            for(int y = -2; y <= 0; y++) {
                for(int z = -2; z <= 2; z++){
                    if (level.getBlockState(new BlockPos(pos.getX()+x, pos.getY()+y, pos.getZ()+z)).is(fruitCropBlock)) {
                        fruits = fruits+1;
                    }
                }
            }
        }
        return fruits == 0;
    }

    @Override
    protected boolean isRandomlyTicking(@NotNull BlockState state) {
        return !state.getValue(PERSISTENT);
    }
}

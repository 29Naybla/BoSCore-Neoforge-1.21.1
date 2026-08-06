package com.x29naybla.bos_core.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PieBlock extends Block {
    public static final MapCodec<PieBlock> CODEC = simpleCodec(PieBlock::new);
    public static final int MAX_BITES = 3;
    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 3);
    protected static final VoxelShape[] SHAPES = {
            Block.box(1.0, 0.0, 1.0, 15.0, 6.0, 15.0),
            Shapes.join(Block.box(8, 0, 1, 15, 6, 15), Block.box(1, 0, 8, 8, 6, 15), BooleanOp.OR),
            Block.box(8, 0, 1, 15, 6, 15),
            Block.box(8, 0, 1, 15, 6, 8)
    };
    protected int FOOD;
    protected float SATURATION;

    @Override
    public @NotNull MapCodec<PieBlock> codec() {
        return CODEC;
    }

    public PieBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BITES, 0));
        this.FOOD = 2;
        this.SATURATION = 0.1f;
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPES[state.getValue(BITES)];
    }
    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            if (eat(level, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }

            if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return eat(level, pos, state, player);
    }

    protected InteractionResult eat(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            player.getFoodData().eat(FOOD, SATURATION);
            int i = state.getValue(BITES);
            level.gameEvent(player, GameEvent.EAT, pos);
            if (i < MAX_BITES) {
                level.setBlock(pos, state.setValue(BITES, i + 1), 3);
            } else {
                level.removeBlock(pos, false);
                level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            }

            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        return facing == Direction.DOWN && !state.canSurvive(level, currentPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isSolid();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BITES);
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, @NotNull Level level, @NotNull BlockPos pos) {
        return getOutputSignal(blockState.getValue(BITES));
    }

    public static int getOutputSignal(int eaten) {
        return (4 - eaten) * 2;
    }

    @Override
    protected boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
        return false;
    }
}

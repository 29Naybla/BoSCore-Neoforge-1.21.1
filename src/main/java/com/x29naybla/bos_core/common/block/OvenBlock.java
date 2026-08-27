package com.x29naybla.bos_core.common.block;

import com.mojang.serialization.MapCodec;
import com.x29naybla.bos_core.common.block.entity.OvenBlockEntity;
import com.x29naybla.bos_core.common.registry.BoSBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OvenBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final MapCodec<OvenBlock> CODEC = simpleCodec(OvenBlock::new);

    public OvenBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(LIT, Boolean.FALSE);
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void animateTick(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource randomSource) {
        if (state.getValue(LIT)) {
            double x = (double) pos.getX() + (double) 0.5F;
            double y = pos.getY();
            double z = (double) pos.getZ() + (double) 0.5F;
            if (randomSource.nextInt(10) == 0) {
                level.playLocalSound((double) pos.getX() + (double) 0.5F, (double) pos.getY() + (double) 0.5F, (double) pos.getZ() + (double) 0.5F, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + randomSource.nextFloat(), randomSource.nextFloat() * 0.7F + 0.6F, false);
            }
            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double r1 = randomSource.nextDouble() * 0.6 - 0.3;
            double r2 = axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52 : r1;
            double r3 = randomSource.nextDouble() * (double) 6.0F / (double) 16.0F;
            double r4 = axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52 : r1;
            level.addParticle(ParticleTypes.SMOKE, x + r2, y + r3, z + r4, 0.0F, 0.0F, 0.0F);
            level.addParticle(ParticleTypes.FLAME, x + r2, y + r3, z + r4, 0.0, 0.0F, 0.0F);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, Level level, @NotNull BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof OvenBlockEntity ovenBlockEntity) {
            ItemStackHandler inventory = ovenBlockEntity.inventory;
            return calcRedstoneFromItemHandler(inventory);
        }
        return 0;
    }

    public static int calcRedstoneFromItemHandler(@Nullable IItemHandlerModifiable handler) {
        if (handler == null) {
            return 0;
        } else {
            int i = 0;
            float f = 0.0F;

            for (int j = 0; j < handler.getSlots(); ++j) {
                ItemStack itemstack = handler.getStackInSlot(j);
                if (!itemstack.isEmpty()) {
                    f += (float) itemstack.getCount() / (float) Math.min(handler.getSlotLimit(j), itemstack.getMaxStackSize());
                    ++i;
                }
            }

            f = f / (float) handler.getSlots();
            return net.minecraft.util.Mth.floor(f * 14.0F) + (i > 0 ? 1 : 0);
        }
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    // BE Stuff
    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new OvenBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof OvenBlockEntity ovenBlockEntity) {
                ovenBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if(!level.isClientSide()) {
            if(level.getBlockEntity(pos) instanceof OvenBlockEntity ovenBlockEntity) {
                player.openMenu(new SimpleMenuProvider(ovenBlockEntity, ovenBlockEntity.getName()), pos);
                return InteractionResult.SUCCESS;
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()) return null;

        return createTickerHelper(blockEntityType, BoSBlockEntities.OVEN.get(), OvenBlockEntity::bakingTick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> serverType, BlockEntityType<E> clientType, BlockEntityTicker<? super E> ticker) {
        return clientType == serverType ? (BlockEntityTicker<A>) ticker : null;
    }
}

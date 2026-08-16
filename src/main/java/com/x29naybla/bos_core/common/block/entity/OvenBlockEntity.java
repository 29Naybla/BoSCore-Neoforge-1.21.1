package com.x29naybla.bos_core.common.block.entity;

import com.x29naybla.bos_core.client.gui.OvenMenu;
import com.x29naybla.bos_core.common.block.OvenBlock;
import com.x29naybla.bos_core.common.recipe.AbstractBakingRecipe;
import com.x29naybla.bos_core.common.registry.BoSBlockEntities;
import com.x29naybla.bos_core.common.registry.BoSRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static com.x29naybla.bos_core.common.block.OvenBlock.LIT;

public class OvenBlockEntity extends BlockEntity implements MenuProvider {
    private static final int[] INGREDIENT_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int FUEL_SLOT = 9;
    private static final int OUTPUT_SLOT = 10;
    protected final ContainerData data;
    private final RecipeManager.CachedCheck<SingleRecipeInputContainer, AbstractBakingRecipe> quickCheck = RecipeManager.createCheck(BoSRecipes.BAKING_TYPE.get());
    private int bakingProgress = 0;
    private int bakingTotalTime = 72;
    private int litTime = 0;
    private int fuelAmount = 0;
    private AbstractBakingRecipe currentRecipe = null;
    public final ItemStackHandler inventory = new ItemStackHandler(11) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            assert level != null;
            if (slot < 9) {
                resetProgress();
            }
        }
    };

    public OvenBlockEntity(BlockPos pos, BlockState blockState) {
        super(BoSBlockEntities.OVEN.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> OvenBlockEntity.this.bakingProgress;
                    case 1 -> OvenBlockEntity.this.bakingTotalTime;
                    case 2 -> OvenBlockEntity.this.litTime;
                    case 3 -> OvenBlockEntity.this.fuelAmount;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: OvenBlockEntity.this.bakingProgress = value;
                    case 1: OvenBlockEntity.this.bakingTotalTime = value;
                    case 2: OvenBlockEntity.this.litTime = value;
                    case 3: OvenBlockEntity.this.fuelAmount = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.oven");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new OvenMenu(containerId, playerInventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    private void setChanged(Level pLevel, BlockPos pPos, BlockState pState, boolean b) {
        pLevel.setBlock(pPos, pState.setValue(LIT, b), 3);
        super.setChanged();
    }

    private boolean isLit() {
        return this.bakingProgress > 0;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("BakeTime", bakingProgress);
        tag.putInt("BakeTimeTotal", bakingTotalTime);
        tag.putInt("LitTime", litTime);
        tag.putInt("FuelAmount", fuelAmount);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);

        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        bakingProgress = tag.getInt("BakeTime");
        bakingTotalTime = tag.getInt("BakeTimeTotal");
        litTime = tag.getInt("LitTime");
        fuelAmount = tag.getInt("FuelAmount");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, OvenBlockEntity ovenBlockEntity) {
        boolean wasLit = ovenBlockEntity.litTime > 0;
        if (ovenBlockEntity.litTime > 0) {
            ovenBlockEntity.litTime--;
        } else {
            ovenBlockEntity.litTime = 0;
        }

        if (hasRecipe(ovenBlockEntity)) {
            ovenBlockEntity.bakingProgress++;
            ovenBlockEntity.setChanged(level, blockPos, blockState, true);
            if (ovenBlockEntity.bakingProgress >= ovenBlockEntity.bakingTotalTime) {
                craftItem(ovenBlockEntity);
            }
        } else {
            ovenBlockEntity.resetProgress();
            if (wasLit && ovenBlockEntity.litTime <= 0) {
                ovenBlockEntity.setChanged(level, blockPos, blockState, false);
            }
        }
    }

    static boolean isFueled(OvenBlockEntity ovenBlockEntity, BlockPos pos, Level level) {
        if (level.isClientSide) return false;
        if (ovenBlockEntity.litTime > 0) {
            ovenBlockEntity.setChanged(level, pos, ovenBlockEntity.getBlockState(), true);
            return true;
        } else {
            ovenBlockEntity.setChanged(level, pos, ovenBlockEntity.getBlockState(), false);
            return false;
        }
    }

    private static void craftItem(OvenBlockEntity ovenBlockEntity) {

        SimpleContainer inventory = new SimpleContainer(ovenBlockEntity.inventory.getSlots());
        for (int i = 0; i < ovenBlockEntity.inventory.getSlots(); i++) {
            inventory.setItem(i, ovenBlockEntity.inventory.getStackInSlot(i));
        }

        var currentRecipe = ovenBlockEntity.currentRecipe;
        if (currentRecipe != null) {
            for (int i = 0; i < 9; ++i) {
                ItemStack slotStack = ovenBlockEntity.inventory.getStackInSlot(i);
                if (slotStack.hasCraftingRemainingItem()) {
                    Direction direction = ovenBlockEntity.getBlockState().getValue(OvenBlock.FACING).getCounterClockWise();
                    double x = (double) ovenBlockEntity.worldPosition.getX() + 0.5 + (double) direction.getStepX() * 0.25;
                    double y = (double) ovenBlockEntity.worldPosition.getY() + 0.7;
                    double z = (double) ovenBlockEntity.worldPosition.getZ() + 0.5 + (double) direction.getStepZ() * 0.25;
                    spawnItemEntity(ovenBlockEntity.level, ovenBlockEntity.inventory.getStackInSlot(i).getCraftingRemainingItem(), x, y, z, (float) direction.getStepX() * 0.08F, 0.25, (float) direction.getStepZ() * 0.08F);
                }
            }

            for (int i = 0; i < 9; ++i) {
                ovenBlockEntity.inventory.extractItem(i, 1, false);
            }
            inventory.getItem(OUTPUT_SLOT).is(currentRecipe.getResultItem().getItem());

            ovenBlockEntity.inventory.setStackInSlot(OUTPUT_SLOT, new ItemStack(currentRecipe.getResultItem().getItem(),
                    ovenBlockEntity.inventory.getStackInSlot(OUTPUT_SLOT).getCount() + ovenBlockEntity.getTheCount(currentRecipe.getResultItem())));

            ovenBlockEntity.resetProgress();

        }
    }

    public static void spawnItemEntity(Level level, ItemStack stack, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
        ItemEntity entity = new ItemEntity(level, x, y, z, stack);
        entity.setDeltaMovement(xMotion, yMotion, zMotion);
        level.addFreshEntity(entity);
    }

    private int getTheCount(ItemStack itemIn) {
        return itemIn.getCount();
    }

    private void resetProgress() {
        bakingProgress = 0;
        bakingTotalTime = 72;
    }

    private static boolean hasRecipe(OvenBlockEntity ovenBlockEntity) {
        Level level = ovenBlockEntity.level;
        BlockPos pos = ovenBlockEntity.getBlockPos();

        SingleRecipeInputContainer input = new SingleRecipeInputContainer(ovenBlockEntity.inventory);
        Optional<RecipeHolder<AbstractBakingRecipe>> recipeMatch = ovenBlockEntity.quickCheck.getRecipeFor(input, level);

        if (recipeMatch.isPresent()) {
            AbstractBakingRecipe recipe = recipeMatch.get().value();
            ItemStack result = recipe.getResultItem(level.registryAccess());
            if (canInsertAmountIntoOutputSlot(ovenBlockEntity.inventory, result)) {
                ovenBlockEntity.currentRecipe = recipe;
                return startCraftIfFueled(ovenBlockEntity, pos, level, recipe);
            }
        }


        return false;
    }

    private static boolean canInsertAmountIntoOutputSlot(ItemStackHandler handler, ItemStack output) {
        ItemStack currentOutput = handler.getStackInSlot(OUTPUT_SLOT);
        if (currentOutput.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(currentOutput, output)) {
            return false;
        }
        return currentOutput.getCount() + output.getCount() <= currentOutput.getMaxStackSize();
    }

    static boolean startCraftIfFueled(OvenBlockEntity ovenBlockEntity, BlockPos pos, Level level, int progress) {
        if (!isFueledWithTier(ovenBlockEntity, pos, level)) {
            if (!ovenBlockEntity.burnFuel(requiredTier))
                return false;
        }
        ovenBlockEntity.bakingTotalTime = progress;
        return true;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public static class SingleRecipeInputContainer implements RecipeInput {
        private final ItemStackHandler handler;

        public SingleRecipeInputContainer(ItemStackHandler handler) {
            this.handler = handler;
        }

        @Override
        public @NotNull ItemStack getItem(int index) {
            return handler.getStackInSlot(index);
        }

        @Override
        public int size() {
            return handler.getSlots();
        }
    }
}

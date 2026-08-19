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
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static com.x29naybla.bos_core.common.block.OvenBlock.LIT;
import static net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.isFuel;

public class OvenBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer, StackedContentsCompatible {
    private static final int[] INGREDIENT_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int FUEL_SLOT = 9;
    private static final int OUTPUT_SLOT = 10;
    protected final ContainerData data;
    private final RecipeManager.CachedCheck<SingleRecipeInputContainer, AbstractBakingRecipe> quickCheck =
            RecipeManager.createCheck(BoSRecipes.BAKING_TYPE.get());
    private int bakingProgress = 0;
    private int bakingTotalTime = 72;
    private int litTime = 0;
    private int fuelAmount = 0;
    private ItemStack activeFuel = ItemStack.EMPTY;
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

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("BakeTime", bakingProgress);
        tag.putInt("BakeTimeTotal", bakingTotalTime);
        tag.putInt("LitTime", litTime);
        tag.putInt("FuelAmount", fuelAmount);
        tag.put("ActiveFuel", activeFuel.saveOptional(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);

        if(tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        bakingProgress = tag.getInt("BakeTime");
        bakingTotalTime = tag.getInt("BakeTimeTotal");
        litTime = tag.getInt("LitTime");
        fuelAmount = tag.getInt("FuelAmount");
        activeFuel = ItemStack.parseOptional(registries, tag.getCompound("ActiveFuel"));
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, OvenBlockEntity ovenBlockEntity) {
        if (isFueled(ovenBlockEntity, pPos, pLevel)) {
            ovenBlockEntity.litTime--;
            if (ovenBlockEntity.litTime <= 0) {
                ovenBlockEntity.activeFuel = ItemStack.EMPTY;
            }
        } else {
            ovenBlockEntity.litTime = 0;
        }

        if (hasRecipe(ovenBlockEntity)) {
            ovenBlockEntity.bakingProgress++;
            ovenBlockEntity.setChanged(pLevel, pPos, pState, true);
            if (ovenBlockEntity.bakingProgress == ovenBlockEntity.bakingTotalTime) {
                craftItem(ovenBlockEntity);
            }
        } else {
            ovenBlockEntity.resetProgress();
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
        var currentRecipe = ovenBlockEntity.currentRecipe;
        if (currentRecipe != null) {
            handleRemainingItem(ovenBlockEntity);

            assert ovenBlockEntity.level != null;
            ItemStack resultStack = currentRecipe.getResultItem(ovenBlockEntity.level.registryAccess());
            ItemStack currentOutput = ovenBlockEntity.inventory.getStackInSlot(OUTPUT_SLOT);
            if (currentOutput.isEmpty()) {
                ovenBlockEntity.inventory.setStackInSlot(OUTPUT_SLOT, resultStack.copy());
            } else {
                currentOutput.grow(resultStack.getCount());
            }

            ovenBlockEntity.resetProgress();
        }
    }

    public static void handleRemainingItem(OvenBlockEntity ovenBlockEntity) {
        for (int i = 0; i < 9; ++i) {
            ItemStack slotStack = ovenBlockEntity.inventory.getStackInSlot(i);
            if(slotStack.hasCraftingRemainingItem()) {
                if(slotStack.getCount() == 1) ovenBlockEntity.inventory.setStackInSlot(i, slotStack.getCraftingRemainingItem());
                else {
                    Direction direction = ovenBlockEntity.getBlockState().getValue(OvenBlock.FACING).getCounterClockWise();
                    double x = (double) ovenBlockEntity.worldPosition.getX() + 0.5 + (double) direction.getStepX() * 0.25;
                    double y = (double) ovenBlockEntity.worldPosition.getY() + 0.7;
                    double z = (double) ovenBlockEntity.worldPosition.getZ() + 0.5 + (double) direction.getStepZ() * 0.25;
                    spawnItemEntity(ovenBlockEntity.level, ovenBlockEntity.inventory.getStackInSlot(i).getCraftingRemainingItem(), x, y, z, (float) direction.getStepX() * 0.08F, 0.25, (float) direction.getStepZ() * 0.08F);
                    ovenBlockEntity.inventory.extractItem(i, 1, false);
                }
            } else {
                ovenBlockEntity.inventory.extractItem(i, 1, false);
            }
        }
    }

    public static void spawnItemEntity(Level level, ItemStack stack, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
        ItemEntity entity = new ItemEntity(level, x, y, z, stack);
        entity.setDeltaMovement(xMotion, yMotion, zMotion);
        level.addFreshEntity(entity);
    }

    private void resetProgress() {
        this.bakingProgress = 0;
        this.bakingTotalTime = 72;
        this.currentRecipe = null;
    }

    private static boolean hasRecipe(OvenBlockEntity ovenBlockEntity) {
        Level level = ovenBlockEntity.level;
        BlockPos pos = ovenBlockEntity.getBlockPos();

        SingleRecipeInputContainer input = new SingleRecipeInputContainer(ovenBlockEntity.inventory);
        assert level != null;
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

    static boolean startCraftIfFueled(OvenBlockEntity ovenBlockEntity, BlockPos pos, Level level, AbstractBakingRecipe recipe) {
        if (!isFueled(ovenBlockEntity, pos, level) || !recipe.fuelMatches(ovenBlockEntity.activeFuel)) {
            if (!ovenBlockEntity.burnFuel(recipe))
                return false;
        }
        ovenBlockEntity.bakingTotalTime = recipe.getCookTime();
        return true;
    }

    private boolean burnFuel(AbstractBakingRecipe recipe) {
        assert this.level != null;
        if (!this.level.isClientSide) {
            var fuel = this.inventory.getStackInSlot(FUEL_SLOT).copy();

            // look for fuel definitions that specifically consider this recipe type
            int burnTime = fuel.getBurnTime(recipe.getType());
            // then prioritize smoking if none exist
            if (burnTime <= 0) {
                burnTime = fuel.getBurnTime(RecipeType.SMOKING);
            }
            // then default furnaces
            if (burnTime <= 0) {
                burnTime = fuel.getBurnTime(RecipeType.SMELTING);
            }

            if (burnTime > 0) {
                this.fuelAmount = burnTime;
                this.litTime = burnTime;
                this.activeFuel = fuel.copyWithCount(1);
                if (fuel.getCount() > 1) {
                    fuel.setCount(fuel.getCount() - 1);
                    this.inventory.setStackInSlot(FUEL_SLOT, fuel);
                } else {
                    this.inventory.setStackInSlot(FUEL_SLOT, fuel.getCraftingRemainingItem());
                }
                return true;
            }

        }
        return false;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        if (side == Direction.UP) {
            return INGREDIENT_SLOTS;
        } else {
            return new int[]{side == Direction.DOWN ? OUTPUT_SLOT : FUEL_SLOT};
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return canPlaceItem(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        if (direction == Direction.DOWN && index == OUTPUT_SLOT) {
            return true;
        }
        return direction != Direction.UP && index == FUEL_SLOT && !isFuel(stack);
    }

    @Override
    public int getContainerSize() {
        return this.inventory.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.inventory.getSlots(); ++i) {
            if (!this.inventory.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.inventory.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return this.inventory.extractItem(slot, amount, false);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return this.inventory.extractItem(slot, 1, false);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.inventory.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        assert this.level != null;
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            this.inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void fillStackedContents(@NotNull StackedContents contents) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            contents.accountStack(this.getItem(i));
        }
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

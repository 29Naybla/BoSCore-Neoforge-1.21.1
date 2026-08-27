package com.x29naybla.bos_core.common.block.entity.container;

import com.x29naybla.bos_core.common.block.entity.OvenBlockEntity;
import com.x29naybla.bos_core.common.registry.BoSBlocks;
import com.x29naybla.bos_core.common.registry.BoSMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OvenMenu extends AbstractContainerMenu {
    public final OvenBlockEntity blockEntity;
    private final ContainerData data;
    public final ItemStackHandler inventory;
    private final ContainerLevelAccess canInteractWithCallable;

    public OvenMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, getBlockEntity(inv, extraData), new SimpleContainerData(4));
    }

    public OvenMenu(int containerId, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(BoSMenuTypes.OVEN_MENU.get(), containerId);
        this.blockEntity = ((OvenBlockEntity) blockEntity);
        this.data = data;
        this.inventory = ((OvenBlockEntity) blockEntity).inventory;
        assert blockEntity.getLevel() != null;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        int index = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                this.addSlot(new SlotItemHandler(this.blockEntity.inventory, index++, 30 + y * 18, 17 + x * 18));
            }
        }

        this.addSlot(new BoSFuelSlot(this.blockEntity.inventory, index++, 124,56));
        this.addSlot(new BoSResultSlot(inv.player, (OvenBlockEntity) blockEntity, this.blockEntity.inventory, index, 124,17));

        addDataSlots(data);
    }

    private static OvenBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (blockEntity instanceof OvenBlockEntity ovenBlockEntity) {
            return ovenBlockEntity;
        }
        throw new IllegalStateException("Block entity is not correct! " + blockEntity);
    }

    public boolean isLit() {
        return data.get(0) > 0;
    }

    public boolean isFueled() {
        return blockEntity.getBlockState().getValue(BlockStateProperties.LIT);
    }

    public int getBurnProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int arrowPixelSize = 24;

        return maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
    }

    public float getLitTime() {
        int litTime = this.data.get(2);
        int fuel = this.data.get(3);
        if (fuel == 0) {
            fuel = 200;
        }

        return Mth.ceil(Mth.clamp((float) litTime / fuel, 0.0F, 1.0F) * 13.0F) + 2;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();


        if(index < 36) {
            //this is player inv
            boolean isFuel = AbstractFurnaceBlockEntity.isFuel(sourceStack);

            if (isFuel && !moveItemStackTo(sourceStack, 36+9, 36+10, false)) {
                if(!moveItemStackTo(sourceStack, 36, 36+9, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if(!moveItemStackTo(sourceStack, 36, 36+9, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index == 36+11) {
            //this is BE output slot
            if(!moveItemStackTo(sourceStack, 0, 36, true)) {
                return ItemStack.EMPTY;
            }
            sourceSlot.onQuickCraft(sourceStack, copyOfSourceStack);
        } else if (index < 36+11) {
            //this is BE inv
            if(!moveItemStackTo(sourceStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;

    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(canInteractWithCallable, player, BoSBlocks.OVEN.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int l = 0; l < 9; l++) {
                this.addSlot(new Slot(playerInventory, l+ i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i< 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}

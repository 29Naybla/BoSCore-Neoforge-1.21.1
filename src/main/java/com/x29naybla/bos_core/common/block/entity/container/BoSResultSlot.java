package com.x29naybla.bos_core.common.block.entity.container;

import com.x29naybla.bos_core.common.block.entity.OvenBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class BoSResultSlot extends SlotItemHandler {
    public final OvenBlockEntity ovenBlockEntity;
    private final Player player;
    private int removeCount;

    public BoSResultSlot(Player player, OvenBlockEntity blockEntity, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.ovenBlockEntity = blockEntity;
        this.player = player;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    @Nonnull
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }

        return super.remove(amount);
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        this.checkTakeAchievements(stack);
        super.onTake(player, stack);
    }

    @Override
    protected void onQuickCraft(@NotNull ItemStack stack, int amount) {
        this.removeCount += amount;
        this.checkTakeAchievements(stack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(this.player.level(), this.player, this.removeCount);

        if (!this.player.level().isClientSide) {
            ovenBlockEntity.awardUsedRecipes(this.player, ovenBlockEntity.getDroppableInventory());
        }

        this.removeCount = 0;
    }
}

package com.x29naybla.bos_core.mixin;

import com.x29naybla.bos_core.common.registry.BoSItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class PumpkinPieItemMixin {
    @Inject(at = @At("TAIL"), method = "useOn", cancellable = true)
    private void bos_useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!context.getItemInHand().is(Items.PUMPKIN_PIE))
            return;

        Player player = context.getPlayer();
        if (player != null) {
            cir.setReturnValue(BoSItems.SHADOW_PUMPKIN_PIE.get().useOn(context));
        }
    }

    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    private void bos_use(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (player.getItemInHand(usedHand).is(Items.PUMPKIN_PIE)) {
            cir.setReturnValue(InteractionResultHolder.pass(player.getItemInHand(usedHand)));
        }
    }
}

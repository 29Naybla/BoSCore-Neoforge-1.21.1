package com.x29naybla.bos_core.mixin;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrame.class)
public abstract class ItemFrameMixin extends HangingEntity {
    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    public abstract void kill();

    protected ItemFrameMixin(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at= {@At(value = "HEAD")}, method = "hurt", cancellable = true)
    private void bos_hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if(this.getItem().is(Items.FILLED_MAP)) {
            this.spawnAtLocation(this.getItem());
            this.gameEvent(GameEvent.BLOCK_CHANGE, source.getEntity());
            this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            this.discard();
        }
        cir.setReturnValue(true);
    }

    @Inject(at={@At(value = "HEAD")}, method = "interact", cancellable = true)
    private void bos_interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        if(player.getItemInHand(hand).is(Items.FILLED_MAP)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}

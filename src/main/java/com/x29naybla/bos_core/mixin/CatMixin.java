package com.x29naybla.bos_core.mixin;

import com.x29naybla.bos_core.common.entity.LeatherCollarMixinAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.item.component.DyedItemColor.LEATHER_COLOR;

@Mixin(Cat.class)
public abstract class CatMixin extends TamableAnimal implements LeatherCollarMixinAccess {
    @Unique
    private static final EntityDataAccessor<Integer> DATA_LEATHER_COLLAR_COLOR = SynchedEntityData.defineId(CatMixin.class, EntityDataSerializers.INT);

    protected CatMixin(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void bos_addToLeatherCollarColor(DyeColor dyeColor) {
        this.entityData.set(DATA_LEATHER_COLLAR_COLOR, bos_makeCollarColor(dyeColor));
    }

    @Override
    public void bos_setLeatherCollarColor(int color) {
        this.entityData.set(DATA_LEATHER_COLLAR_COLOR, color);
    }

    @Override
    public int bos_getLeatherCollarColor() {
        return this.entityData.get(DATA_LEATHER_COLLAR_COLOR);
    }

    @Inject(at= {@At("TAIL")}, method = {"defineSynchedData"})
    private void bos_registerData(SynchedEntityData.Builder builder, CallbackInfo ci){
        builder.define(DATA_LEATHER_COLLAR_COLOR, LEATHER_COLOR);
    }

    @Inject(at= {@At("TAIL")}, method = {"addAdditionalSaveData"})
    public void bos_addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("LeatherCollarColor", this.bos_getLeatherCollarColor());
    }

    @Inject(at={@At("TAIL")}, method = {"readAdditionalSaveData"})
    public void bos_readAdditionalSaveData(CompoundTag compound, CallbackInfo ci){
        this.bos_setLeatherCollarColor(compound.getInt("LeatherCollarColor"));
    }

    @Inject(at= {@At("HEAD")}, method = {"mobInteract"}, cancellable = true)
    public void bos_mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        if(!this.level().isClientSide && this.isTame() && player.getItemInHand(hand).getItem() instanceof DyeItem dyeitem && this.isOwnedBy(player)) {
            DyeColor dyecolor = dyeitem.getDyeColor();
            this.bos_addToLeatherCollarColor(dyecolor);
            if(this.hasCustomName())
                this.setCustomName(Component.literal(this.getName().getString()).setStyle(Style.EMPTY.withColor(this.bos_getLeatherCollarColor())));
            player.getItemInHand(hand).consume(1, player);

            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Unique
    private int bos_makeCollarColor(DyeColor dyeColor) {
        int currentColor = this.entityData.get(DATA_LEATHER_COLLAR_COLOR);
        int addedColor = dyeColor.getTextureDiffuseColor();

        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int i1 = 0;

        int j1 = FastColor.ARGB32.red(currentColor);
        int k1 = FastColor.ARGB32.green(currentColor);
        int l1 = FastColor.ARGB32.blue(currentColor);
        l += Math.max(j1, Math.max(k1, l1));
        i += j1;
        j += k1;
        k += l1;
        i1++;

        int i2 = FastColor.ARGB32.red(addedColor);
        int j2 = FastColor.ARGB32.green(addedColor);
        int k2 = FastColor.ARGB32.blue(addedColor);
        l += Math.max(i2, Math.max(j2, k2));
        i += i2;
        j += j2;
        k += k2;
        i1++;

        int l2 = i / i1;
        int i3 = j / i1;
        int k3 = k / i1;
        float f = (float)l / (float)i1;
        float f1 = (float)Math.max(l2, Math.max(i3, k3));
        l2 = (int)((float)l2 * f / f1);
        i3 = (int)((float)i3 * f / f1);
        k3 = (int)((float)k3 * f / f1);
        return FastColor.ARGB32.color(0, l2, i3, k3);
    }

}

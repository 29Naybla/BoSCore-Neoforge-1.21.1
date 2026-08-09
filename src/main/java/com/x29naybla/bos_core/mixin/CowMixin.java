package com.x29naybla.bos_core.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Cow.class)
public abstract class CowMixin extends Animal {
    @Unique
    private static final EntityDataAccessor<Boolean> CAN_MILK = SynchedEntityData.defineId(CowMixin.class, EntityDataSerializers.BOOLEAN);
    @Unique
    public int bos_milkTime;
    @Unique
    public final int bos_maxMilkTime = 24000;

    protected CowMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.bos_milkTime = 0;
    }

    /**
     * @author 29Naybla
     * @reason Milking cooldown
     */
    @Overwrite
    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack heldItemStack = player.getItemInHand(hand);
        if (!this.level().isClientSide) {
            if (heldItemStack.is(Items.BUCKET) && !this.isBaby()) {
                if (this.bos_milkTime == 0) {
                    this.level().playSound(null, this.getOnPos(), SoundEvents.COW_MILK, SoundSource.PLAYERS);
                    ItemStack milkItemStack = ItemUtils.createFilledResult(heldItemStack, player, Items.MILK_BUCKET.getDefaultInstance());
                    player.setItemInHand(hand, milkItemStack);
                    this.bos_milkTime = bos_maxMilkTime;
                    this.entityData.set(CAN_MILK, false);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                } else {
                    this.level().playSound(null, this.getOnPos(), SoundEvents.COW_HURT, SoundSource.AMBIENT);
                    this.level().broadcastEntityEvent(this, (byte)13);
                    return InteractionResult.FAIL;
                }
            } else {
                return InteractionResult.PASS;
            }
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    public void aiStep(){
        super.aiStep();
        if(!this.level().isClientSide && this.isAlive() && !this.isBaby() && this.bos_milkTime > 0) {
            if (this.bos_milkTime == 1) {
                this.level().broadcastEntityEvent(this, (byte)14);
                this.entityData.set(CAN_MILK, true);
            }
            this.bos_milkTime--;
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        double rY = this.random.nextGaussian() * 0.02;

        if (id == 13) {
            this.level().addParticle(ParticleTypes.ANGRY_VILLAGER, this.getX(), this.getEyeY() + 0.1, this.getZ(), 0, rY, 0);
        } else if (id == 14) {
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getEyeY() + 0.4, this.getZ(), 0, rY, 0);
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getX() + 0.1, this.getEyeY() + 0.2, this.getZ() - 0.1, 0, rY, 0);
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getX() - 0.1, this.getEyeY() + 0.3, this.getZ() + 0.1, 0, rY, 0);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Unique
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound){
        super.addAdditionalSaveData(compound);
        compound.putInt("MilkReadyTime", this.bos_milkTime);
    }

    @Unique
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound){
        super.readAdditionalSaveData(compound);
        if (compound.contains("MilkReadyTime")) {
            this.bos_milkTime = compound.getInt("MilkReadyTime");
        }
    }

    @Unique
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder){
        super.defineSynchedData(builder);
        builder.define(CAN_MILK, true);
    }
}

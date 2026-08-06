package com.x29naybla.bos_core.mixin;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ocelot.class)
public abstract class OcelotMixin extends Animal {
    @Shadow
    public abstract boolean isFood(@NotNull ItemStack stack);

    protected OcelotMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void bos_mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (this.isFood(itemStack)) {
            this.usePlayerItem(player, hand, itemStack);
            if (!this.level().isClientSide) {
                this.bos_tryToTame(player);
                this.setPersistenceRequired();
                cir.setReturnValue(InteractionResult.SUCCESS);
            } else {
                cir.setReturnValue(super.mobInteract(player, hand));
            }
        }
    }

    @Override
    protected void usePlayerItem(@NotNull Player player, @NotNull InteractionHand hand, @NotNull ItemStack stack) {
        if (this.isFood(stack)) {
            this.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
        }

        super.usePlayerItem(player, hand, stack);
    }

    @Unique
    private void bos_tryToTame(Player player) {
        if (this.random.nextInt(3) == 0  && !net.neoforged.neoforge.event.EventHooks.onAnimalTame(this, player)) {
            Cat cat = EntityType.CAT.create(this.level());

            if (cat != null) {
                cat.copyPosition(this);
                cat.setBaby(this.isBaby());
                cat.setNoAi(this.isNoAi());
                if (this.hasCustomName()) {
                    cat.setCustomName(this.getCustomName());
                    cat.setCustomNameVisible(this.isCustomNameVisible());
                }

                if (this.isPersistenceRequired()) {
                    cat.setPersistenceRequired();
                }

                cat.setInvulnerable(this.isInvulnerable());
                cat.setCanPickUpLoot(this.canPickUpLoot());

                for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                    ItemStack itemstack = this.getItemBySlot(equipmentslot);
                    if (!itemstack.isEmpty()) {
                        cat.setItemSlot(equipmentslot, itemstack.copyAndClear());
                        cat.setDropChance(equipmentslot, this.getEquipmentDropChance(equipmentslot));
                    }
                }
                cat.setOrderedToSit(true);
                BuiltInRegistries.CAT_VARIANT.getRandom(random).ifPresent(cat::setVariant);

                this.level().addFreshEntity(cat);
                if (this.isPassenger()) {
                    Entity entity = this.getVehicle();
                    if (entity != null) {
                        this.stopRiding();
                        cat.startRiding(entity, true);
                    }
                }

                this.discard();
                cat.tame(player);
                cat.level().broadcastEntityEvent(cat, (byte) 7);
            }
        } else {
            this.level().broadcastEntityEvent(this, (byte) 6);
        }
    }

    /**
     * @author 29Naybla
     * @reason Replace with taming animal handling
     */
    @Overwrite
    public void handleEntityEvent(byte id) {
        if (id == 7) {
            this.bos_spawnTamingParticles(true);
        } else if (id == 6) {
            this.bos_spawnTamingParticles(false);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Unique
    protected void bos_spawnTamingParticles(boolean tamed) {
        ParticleOptions particleoptions = ParticleTypes.HEART;
        if (!tamed) {
            particleoptions = ParticleTypes.SMOKE;
        }

        for (int i = 0; i < 7; i++) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(particleoptions, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
        }
    }
}

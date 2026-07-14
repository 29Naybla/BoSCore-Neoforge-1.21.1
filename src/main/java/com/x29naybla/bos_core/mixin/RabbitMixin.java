package com.x29naybla.bos_core.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.x29naybla.bos_core.common.entity.RabbitMixinAccess;
import com.x29naybla.bos_core.common.registry.BoSTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Rabbit.class)
public abstract class RabbitMixin extends Animal implements RabbitMixinAccess {
    @Unique
    private static final EntityDataAccessor<Boolean> IS_FLUFFY = SynchedEntityData.defineId(Rabbit.class, EntityDataSerializers.BOOLEAN);

    protected RabbitMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at= {@At("TAIL")}, method = {"finalizeSpawn"})
    private void bos_finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir){
        if(level.getBiome(this.blockPosition()).is(BoSTags.Biomes.SPAWNS_FLUFFY_RABBITS)){
            bos_setFluffy(true);
        }
    }

    @ModifyReturnValue(at= {@At(value = "TAIL")}, method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Rabbit;")
    private Rabbit bos_bredRabbitIsFluffy(Rabbit original, ServerLevel serverLevel, AgeableMob ageableMob) {
        if(original != null){
            if(serverLevel.getBiome(original.getOnPos()).is(BoSTags.Biomes.SPAWNS_FLUFFY_RABBITS)){
               ((RabbitMixinAccess) original).bos_setFluffy(true);
            }
        }
        return original;
    }

    @Inject(at= {@At("TAIL")}, method = {"defineSynchedData"})
    private void bos_registerData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(IS_FLUFFY, false);
    }

    @Inject(at= {@At("TAIL")}, method = {"addAdditionalSaveData"})
    private void bos_writeAdditional(CompoundTag compoundTag, CallbackInfo callbackInfo) {
        compoundTag.putBoolean("IsFluffy", this.bos_isFluffy());
    }

    @Inject(at= {@At("TAIL")}, method = {"readAdditionalSaveData"})
    private void bos_readAdditional(CompoundTag compoundTag, CallbackInfo ci) {
        this.bos_setFluffy(compoundTag.getBoolean("IsFluffy"));
    }

    @Unique
    public boolean bos_isFluffy() {
        return this.entityData.get(IS_FLUFFY);
    }

    @Override
    public void bos_setFluffy(boolean value) {
        this.entityData.set(IS_FLUFFY, value);
    }
}

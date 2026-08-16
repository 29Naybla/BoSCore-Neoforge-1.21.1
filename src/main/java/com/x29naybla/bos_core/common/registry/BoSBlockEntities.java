package com.x29naybla.bos_core.common.registry;

import com.x29naybla.bos_core.BoSCore;
import com.x29naybla.bos_core.common.block.entity.OvenBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BoSBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BoSCore.MODID);

    public static final Supplier<BlockEntityType<OvenBlockEntity>> OVEN =
            BLOCK_ENTITIES.register("oven", () -> BlockEntityType.Builder.of(
                    OvenBlockEntity::new, BoSBlocks.OVEN.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

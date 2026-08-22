package com.x29naybla.bos_core.common.registry;

import com.x29naybla.bos_core.BoSCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

public class BoSTrees {
    private static final ResourceKey<ConfiguredFeature<?, ?>> APPLE_TREE_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(BoSCore.MODID, "apple_tree"));

    public static final TreeGrower APPLE_TREE = new TreeGrower(BoSCore.MODID + ":apple_tree",
            Optional.empty(), Optional.of(APPLE_TREE_KEY), Optional.empty());
}

package com.x29naybla.bos_core.common.registry;

import com.x29naybla.bos_core.BoSCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class BoSTags {
    public static class Biomes {
        public static final TagKey<Biome> SPAWNS_FLUFFY_RABBITS = createTag("spawns_fluffy_rabbits");


        private static TagKey<Biome> createTag(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(BoSCore.MODID, name));
        }
    }
}

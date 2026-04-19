package com.isles.worldgen;

import com.isles.blest;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public final class ModBiomes {
    public static final ResourceKey<Biome> SKY_FOREST =
            ResourceKey.create(Registries.BIOME, new ResourceLocation(blest.MODID, "sky_forest"));
    public static final ResourceKey<Biome> LARGE_SKY_FOREST =
            ResourceKey.create(Registries.BIOME, new ResourceLocation(blest.MODID, "large_sky_forest"));
    public static final ResourceKey<Biome> THE_INFECTION_LANDS =
            ResourceKey.create(Registries.BIOME, new ResourceLocation(blest.MODID, "the_infection_lands"));

    private ModBiomes() {
    }
}

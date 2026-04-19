package com.isles.worldgen;

import com.isles.blest;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class SkyForestRegion extends Region {
    public SkyForestRegion(int weight) {
        super(new ResourceLocation(blest.MODID, "sky_forest_region"), RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addBiome(
                mapper,
                Climate.Parameter.span(-0.5F, 0.9F),
                Climate.Parameter.span(-0.3F, 0.9F),
                Climate.Parameter.span(-1.0F, 1.0F),
                Climate.Parameter.span(-1.0F, 1.0F),
                Climate.Parameter.span(-1.0F, 1.0F),
                Climate.Parameter.span(-1.0F, 1.0F),
                0.0F,
                ModBiomes.SKY_FOREST
        );
        this.addBiome(
                mapper,
                Climate.Parameter.span(-0.5F, 0.9F),
                Climate.Parameter.span(-0.3F, 0.9F),
                Climate.Parameter.span(-1.0F, 1.0F),
                Climate.Parameter.span(-1.0F, 1.0F),
                Climate.Parameter.span(-1.0F, 1.0F),
                Climate.Parameter.span(0.0F, 1.0F),
                0.0F,
                ModBiomes.LARGE_SKY_FOREST
        );
    }
}

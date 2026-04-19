package com.isles.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

public class InfectionBiomeSource extends BiomeSource {
    public static final MapCodec<InfectionBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.CODEC.fieldOf("base_biome").forGetter(s -> s.baseBiome),
            Biome.CODEC.fieldOf("infection_biome").forGetter(s -> s.infectionBiome)
    ).apply(instance, InfectionBiomeSource::new));

    private final Holder<Biome> baseBiome;
    private final Holder<Biome> infectionBiome;

    public InfectionBiomeSource(Holder<Biome> baseBiome, Holder<Biome> infectionBiome) {
        this.baseBiome = baseBiome;
        this.infectionBiome = infectionBiome;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        // x and z are biome coordinates (1 unit = 4 blocks)
        // (100, 100)   -> (25, 25)
        // (-100, 100)  -> (-25, 25)
        // (-100, -100) -> (-25, -25)
        int radius = 24; // About 96 blocks radius

        if (isNear(x, z, 25, 25, radius) || isNear(x, z, -25, 25, radius) || isNear(x, z, -25, -25, radius)) {
            return infectionBiome;
        }
        return baseBiome;
    }

    private boolean isNear(int x, int z, int targetX, int targetZ, int radius) {
        double dx = x - targetX;
        double dz = z - targetZ;
        return dx * dx + dz * dz <= radius * radius;
    }

    @Override
    public Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.of(baseBiome, infectionBiome);
    }
}

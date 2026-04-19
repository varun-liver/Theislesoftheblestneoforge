package com.isles.worldgen;

import com.isles.blest;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public final class BlestSurfaceRules {
    private BlestSurfaceRules() {
    }

    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.RuleSource skyGrass = SurfaceRules.state(blest.sky_grass.get().defaultBlockState());
        SurfaceRules.RuleSource dirt = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
        SurfaceRules.RuleSource infectionGrass = SurfaceRules.state(blest.infection_grass.get().defaultBlockState());
        SurfaceRules.RuleSource infection = SurfaceRules.state(blest.infection.get().defaultBlockState());

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(ModBiomes.SKY_FOREST, ModBiomes.LARGE_SKY_FOREST),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, skyGrass),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, dirt)
                        )
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(ModBiomes.THE_INFECTION_LANDS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, 
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(net.minecraft.world.level.levelgen.Noises.PATCH, 0.0D), infectionGrass),
                                                infection
                                        )
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, infection)
                        )
                )
        );
    }

}

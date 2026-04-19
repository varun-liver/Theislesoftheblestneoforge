package com.isles.worldgen;

import com.isles.blest;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

public class InfectionTreeFeature extends Feature<NoneFeatureConfiguration> {
    public InfectionTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        
        // Randomly pick one of the three structures for regular trees
        String[] structures = {"infection_trees", "infection_trees_two", "large_infection_tree"};
        String selectedStructure = structures[context.random().nextInt(structures.length)];
        
        return placeStructure(level, pos, context.random(), selectedStructure);
    }

    private boolean placeStructure(WorldGenLevel level, BlockPos pos, net.minecraft.util.RandomSource random, String name) {
        StructureTemplateManager manager = level.getLevel().getStructureManager();
        Optional<StructureTemplate> template = manager.get(new ResourceLocation(blest.MODID, name));
        
        if (template.isPresent()) {
            StructurePlaceSettings settings = new StructurePlaceSettings().setIgnoreEntities(true);
            net.minecraft.core.Vec3i size = template.get().getSize();
            
            BlockPos placementPos = pos.offset(-size.getX() / 2, 0, -size.getZ() / 2);
            
            // Check if there is valid ground
            if (level.getBlockState(pos.below()).is(blest.infection.get()) || level.getBlockState(pos.below()).is(blest.infection_grass.get())) {
                template.get().placeInWorld(level, placementPos, placementPos, settings, random, 2);
                return true;
            }
        }
        return false;
    }
}

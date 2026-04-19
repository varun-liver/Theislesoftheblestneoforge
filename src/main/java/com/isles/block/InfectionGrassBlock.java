package com.isles.block;

import com.isles.Config;
import com.isles.blest;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;

import com.isles.InfectionBlockEntity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

public class InfectionGrassBlock extends SpreadingSnowyDirtBlock implements EntityBlock {
    public static final MapCodec<InfectionGrassBlock> CODEC = simpleCodec(InfectionGrassBlock::new);

    @Override
    public MapCodec<InfectionGrassBlock> codec() {
        return CODEC;
    }

    public InfectionGrassBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InfectionBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(net.minecraft.world.level.Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof InfectionBlockEntity infectionBE) {
                infectionBE.tick(level1, pos, state1);
            }
        };
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return super.canSurvive(state, level, pos);
    }
}

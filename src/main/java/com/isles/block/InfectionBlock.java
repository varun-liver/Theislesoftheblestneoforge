package com.isles.block;

import com.isles.Config;
import com.isles.InfectionBlockEntity;
import com.isles.blest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

import com.mojang.serialization.MapCodec;

public class InfectionBlock extends Block implements EntityBlock {
    public static final MapCodec<InfectionBlock> CODEC = simpleCodec(InfectionBlock::new);

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public InfectionBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof InfectionBlockEntity infectionBE) {
                infectionBE.tick(level1, pos, state1);
            }
        };
    }

    private void infect(ServerLevel level, BlockPos pos, BlockState oldState) {
        level.setBlock(pos, blest.infection.get().defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof InfectionBlockEntity infectionBE) {
            infectionBE.setSavedState(oldState);
        }
    }

    private void revert(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof InfectionBlockEntity infectionBE) {
            BlockState saved = infectionBE.getSavedState();
            if (saved != null && !saved.isAir()) {
                level.setBlock(pos, saved, 3);
            } else {
                // If somehow no state was saved, maybe default to air or something else?
                // But we should try to avoid this.
                level.destroyBlock(pos, false);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InfectionBlockEntity(pos, state);
    }
}

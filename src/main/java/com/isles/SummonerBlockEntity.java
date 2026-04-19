package com.isles;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SummonerBlockEntity extends BlockEntity {
    private boolean[] summoned = new boolean[4]; // Tracks summoned status for each type (0: infection, 1: guardian, 2: cursed ones)
    private int summontype;

    public SummonerBlockEntity(BlockPos pos, BlockState state) {
        super(blest.SUMMONER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        // Load the boolean array from individual tags or a bitmask/array if needed.
        // For simplicity and clarity, we'll use individual tags "Summoned_0", "Summoned_1", etc.
        for (int i = 0; i < summoned.length; i++) {
            summoned[i] = tag.getBoolean("Summoned_" + i);
        }
        // Backward compatibility for the old "Summoned" tag
        if (tag.contains("Summoned") && !tag.contains("Summoned_" + tag.getInt("SummonType"))) {
            boolean oldSummoned = tag.getBoolean("Summoned");
            int oldType = tag.getInt("SummonType");
            if (oldSummoned && oldType >= 0 && oldType < summoned.length) {
                summoned[oldType] = true;
            }
        }
        summontype = tag.getInt("SummonType");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        for (int i = 0; i < summoned.length; i++) {
            tag.putBoolean("Summoned_" + i, summoned[i]);
        }
        tag.putInt("SummonType", summontype);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SummonerBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        Player player = level.getNearestPlayer(
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                5.0,
                false
        );
        if (player != null) {
            int currentType = blockEntity.summontype;
            if (currentType >= 0 && currentType < blockEntity.summoned.length) {
                if (!blockEntity.summoned[currentType]) {
                    Entity entity = null;
                    switch (currentType) {
                        case 0:
                            entity = blest.the_infection.get().create(level);
                            break;
                        case 1:
                            entity = blest.the_guardian.get().create(level);
                            break;
                        case 2:
                            entity = blest.the_cursed_ones.get().create(level);
                            break;
                        case 3:
                            entity = blest.Infection_Guardians.get().create(level);
                            break;
                        default:
                            break;
                    }
                    if (entity != null) {
                        entity.moveTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0, 0);
                        level.addFreshEntity(entity);
                    }
                    blockEntity.summoned[currentType] = true;
                    blockEntity.markDirty();
                }
            }
        }
    }

    private void markDirty() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}

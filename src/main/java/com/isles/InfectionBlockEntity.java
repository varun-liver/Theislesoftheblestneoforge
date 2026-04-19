package com.isles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InfectionBlockEntity extends BlockEntity {
    public static final Set<InfectionBlockEntity> INSTANCES = ConcurrentHashMap.newKeySet();
    private BlockState savedState = blest.sky_grass.get().defaultBlockState();
    private int revertDelay = -1;
    private int spreadTimer = -1;
    private Block[] avoid = {
            Blocks.POLISHED_DEEPSLATE,
            Blocks.SMOOTH_STONE,
            Blocks.MOSSY_COBBLESTONE_STAIRS,
            Blocks.GLOWSTONE,
            blest.summoner.get()
    };
    public InfectionBlockEntity(BlockPos pos, BlockState state) {
        super(blest.INFECTION_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide) {
            INSTANCES.add(this);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level != null && !this.level.isClientSide) {
            INSTANCES.remove(this);
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        INSTANCES.remove(this);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        if (Config.spreadInfection) {
            if (spreadTimer == -1) {
                // Initial spread timer: 8 minutes (9600 ticks) + random offset to stagger
                spreadTimer = 6000 + level.random.nextInt(100);
            }

            if (spreadTimer > 0) {
                spreadTimer--;
            } else {
                spread(level, pos, state);
                spreadTimer = 6000; // Reset to 8 minutes
            }
        } else {
            if (revertDelay == -1) {
                revertDelay = level.random.nextInt(181) + 20; // 20 to 200
            }

            if (revertDelay > 0) {
                revertDelay--;
            } else {
                revert(level, pos);
            }
        }
    }

    public void revert(Level level, BlockPos pos) {
        if (savedState != null && !savedState.isAir()) {
            level.setBlock(pos, savedState, 3);
        } else {
            level.destroyBlock(pos, false);
        }
    }

    public static void revertAll() {
        for (InfectionBlockEntity be : INSTANCES) {
            if (be.level != null && !be.level.isClientSide) {
                be.revert(be.level, be.worldPosition);
            }
        }
        INSTANCES.clear();
    }

    public void spread(Level level, BlockPos pos, BlockState state) {
        if (state.is(blest.infection.get())) {
            Direction dir = Direction.getRandom(level.random);
            BlockPos targetPos = pos.relative(dir);
            BlockState targetState = level.getBlockState(targetPos);

            if (!targetState.isAir() && !targetState.is(blest.infection.get()) && !targetState.is(blest.infection_grass.get()) && !Arrays.asList(avoid).contains(targetState.getBlock())) {
                infect(level, targetPos, targetState);
                if (this.level != null) {
                    this.level.playSound(null, this.getBlockPos(), blest.Infection_SPREAD.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        } else if (state.is(blest.infection_grass.get())) {
            for (int i = 0; i < 4; i++) {
                BlockPos targetPos = pos.offset(
                        level.random.nextInt(3) - 1,
                        level.random.nextInt(5) - 3,
                        level.random.nextInt(3) - 1
                );
                if (level.getBlockState(targetPos).is(Blocks.DIRT)) {
                    BlockState newState = blest.infection_grass.get().defaultBlockState();
                    level.setBlockAndUpdate(targetPos, newState);
                }
            }
        }
    }

    private void infect(Level level, BlockPos pos, BlockState oldState) {
        level.setBlock(pos, blest.infection.get().defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof InfectionBlockEntity infectionBE) {
            infectionBE.setSavedState(oldState);
        }
    }

    public void setSavedState(BlockState state) {
        this.savedState = state;
        this.setChanged();
    }

    public BlockState getSavedState() {
        return savedState;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("SavedState", NbtUtils.writeBlockState(savedState));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("SavedState", 10)) {
            this.savedState = NbtUtils.readBlockState(registries.lookupOrThrow(Registries.BLOCK), tag.getCompound("SavedState"));
        }
    }

    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

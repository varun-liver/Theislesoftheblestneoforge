package com.isles.portal;

import com.isles.blest;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;

public class CloudPortalBlock extends NetherPortalBlock {
    private static final ResourceKey<Level> SKY_REALM =
            ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION,
                    new ResourceLocation(blest.MODID, "sky_realm"));

    public CloudPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) {
            return;
        }
        if (player.isOnPortalCooldown()) {
            return;
        }
        ResourceKey<Level> currentDim = player.level().dimension();
        ResourceKey<Level> targetKey = currentDim.equals(SKY_REALM) ? Level.OVERWORLD : SKY_REALM;
        ServerLevel target = player.server.getLevel(targetKey);
        if (target == null) {
            return;
        }
        player.setPortalCooldown();
        player.changeDimension(target, new CloudTeleporter());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state;
    }
}

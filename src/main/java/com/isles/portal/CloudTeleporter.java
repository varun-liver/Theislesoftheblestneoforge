package com.isles.portal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.portal.PortalForcer;
import net.neoforged.neoforge.common.util.ITeleporter;

import java.util.function.Function;

public class CloudTeleporter implements ITeleporter {
    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentLevel, ServerLevel destination,
                              float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity placed = repositionEntity.apply(false);
        if (placed instanceof Player player) {
            player.setPortalCooldown();
        }
        return placed;
    }

    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destination,
                                    Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        PortalInfo info = defaultPortalInfo.apply(destination);
        if (info != null) {
            // Nudge the spawn out of the portal block to avoid instant re-teleport.
            return new PortalInfo(info.pos.add(1.0, 0.0, 1.0), info.speed, info.yRot, info.xRot);
        }
        return new PortalInfo(entity.position().add(1.0, 0.0, 1.0), entity.getDeltaMovement(),
                entity.getYRot(), entity.getXRot());
    }
}

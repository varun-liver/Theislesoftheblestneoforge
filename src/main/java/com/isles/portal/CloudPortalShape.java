package com.isles.portal;

import com.isles.blest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class CloudPortalShape {
    private static final int FRAME_WIDTH = 4;  // total width including frame
    private static final int FRAME_HEIGHT = 5; // total height including frame

    private CloudPortalShape() {
    }

    public static boolean trySpawnPortal(Level level, BlockPos pos) {
        for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}) {
            BlockPos origin = findFrameOrigin(level, pos, axis);
            if (origin != null) {
                createPortal(level, origin, axis);
                return true;
            }
        }
        return false;
    }

    private static BlockPos findFrameOrigin(Level level, BlockPos pos, Direction.Axis axis) {
        for (int dx = 0; dx < FRAME_WIDTH; dx++) {
            for (int dy = 0; dy < FRAME_HEIGHT; dy++) {
                BlockPos origin = axis == Direction.Axis.X
                        ? pos.offset(-dx, -dy, 0)
                        : pos.offset(0, -dy, -dx);
                if (isFrameAt(level, origin, axis)) {
                    return origin;
                }
            }
        }
        return null;
    }

    private static boolean isFrameAt(Level level, BlockPos origin, Direction.Axis axis) {
        for (int w = 0; w < FRAME_WIDTH; w++) {
            for (int h = 0; h < FRAME_HEIGHT; h++) {
                boolean border = w == 0 || w == FRAME_WIDTH - 1 || h == 0 || h == FRAME_HEIGHT - 1;
                BlockPos checkPos = axis == Direction.Axis.X
                        ? origin.offset(w, h, 0)
                        : origin.offset(0, h, w);
                BlockState state = level.getBlockState(checkPos);
                if (border) {
                    if (!state.is(blest.cloud.get())) {
                        return false;
                    }
                } else {
                    if (!state.isAir() && !state.is(blest.cloud_portal.get())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void createPortal(Level level, BlockPos origin, Direction.Axis axis) {
        BlockState portalState = blest.cloud_portal.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_AXIS, axis);
        for (int w = 1; w < FRAME_WIDTH - 1; w++) {
            for (int h = 1; h < FRAME_HEIGHT - 1; h++) {
                BlockPos placePos = axis == Direction.Axis.X
                        ? origin.offset(w, h, 0)
                        : origin.offset(0, h, w);
                level.setBlock(placePos, portalState, 18);
            }
        }
    }
}

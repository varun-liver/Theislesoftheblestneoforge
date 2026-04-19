package com.isles.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.util.EnumSet;
import net.minecraft.world.item.ItemStack;
import com.isles.blest;
public class ThewhispererEntity extends Animal {
    public boolean start;
    private Player followTarget;
    private boolean greeted;

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    public ThewhispererEntity(EntityType<? extends ThewhispererEntity> type, Level level) {
        super(type, level);
        start = false;
        greeted = false;
    }

    public void setFollowTarget(Player player) {
        this.start = true;
        this.followTarget = player;
        this.greeted = false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FollowPlayerGoal(this, 1.2D));
    }
    @Override
    public void aiStep() {
        super.aiStep();
        // Movement handled by FollowPlayerGoal
    }
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    private static final class FollowPlayerGoal extends Goal {
        private final ThewhispererEntity mob;
        private final double speed;
        private int waitTicks = 0;
        private int step = 0;
        @Override
        public void tick() {
            Player target = mob.followTarget;
            if (target == null) return;

            double dist = mob.distanceTo(target);

            if (dist <= 2.0) {
                mob.getNavigation().stop();
                if (!mob.greeted && !mob.level().isClientSide) {
                    if (waitTicks > 0) {
                        waitTicks--;
                        return;
                    }
                    if (step == 0) {
                        target.sendSystemMessage(Component.literal("Help!"));
                        step = 1;
                        waitTicks = 20;
                    } else if (step == 1) {
                        target.sendSystemMessage(Component.literal("They Invaded us!"));
                        step = 2;
                      waitTicks = 20;
                    } else if (step == 2) {
                        target.sendSystemMessage(Component.literal("They took everything"));
                        step = 3;
                        waitTicks = 20;
                    } else if (step == 3) {
                        target.sendSystemMessage(Component.literal("Well, I suppose I owe you a explanation"));
                        step = 4;
                        waitTicks = 20;
                    } else if (step == 4) {
                        target.sendSystemMessage(Component.literal("There is a monster in the infection lands"));
                        step = 5;
                        waitTicks = 20;
                    } else if (step == 5) {
                        target.sendSystemMessage(Component.literal("The only way to beat him is by using the legendary sword called Harpe"));
                        step = 6;
                        waitTicks = 20;
                    } else if (step == 6) {
                        BlockPos towerPos = null;
                        if (mob.level() instanceof ServerLevel serverLevel) {
                            towerPos = blest.getTowerCoords(serverLevel, target.blockPosition());
                        }
                        String coords = towerPos != null ? towerPos.getX() + " " + towerPos.getZ() : "unknown coordinates";
                        target.sendSystemMessage(Component.literal("Go to the portal at " + coords));
                        step = 7;
                        waitTicks = 20;
                    } else if (step == 7) {
                        target.sendSystemMessage(Component.literal("You must do it"));
                        step = 8;
                        waitTicks = 20;
                    } else if (step == 8) {
                        target.sendSystemMessage(Component.literal("For the Isles Of The Blest"));
                        step = 9;
                        waitTicks = 20;
                    } else if (step == 9) {
                        mob.greeted = true;
                        if (!mob.level().isClientSide) {
                            mob.discard(); // no death animation, no drops
                        }
                    }
                }
                return;
            }

            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            mob.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), speed);
        }

        private FollowPlayerGoal(ThewhispererEntity mob, double speed) {
            this.mob = mob;
            this.speed = speed;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }
        @Override
        public boolean canUse() {
            if (!mob.start) {
                return false;
            }
            Player target = mob.followTarget;
            if (target == null || !target.isAlive()) {
                target = mob.level().getNearestPlayer(mob, 16.0);
                mob.followTarget = target;
            }
            return target != null;
        }
        @Override
        public boolean canContinueToUse() {
            return canUse();
        }
    }

}

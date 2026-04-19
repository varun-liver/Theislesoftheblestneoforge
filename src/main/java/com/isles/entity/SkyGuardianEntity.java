package com.isles.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.AnimationState;

public class SkyGuardianEntity extends Monster {
    private static final float ATTACK_1_DAMAGE = 4.0F;
    private static final int ATTACK_1_HIT_TICK = 5;
    private static final int ATTACK_1_LENGTH = 10;

    private static final float ATTACK_2_DAMAGE = 7.0F;
    private static final int ATTACK_2_HIT_TICK = 18;
    private static final int ATTACK_2_LENGTH = 45;

    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState attack2AnimationState = new AnimationState();

    private int currentAttackType = 0;
    private int currentAttackTick = 0;
    private boolean attackDidDamage = false;

    public SkyGuardianEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SkyGuardianMeleeAttackGoal(this, 1.15D, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.tickCustomAttack();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attack2AnimationState.stop();
            this.attackAnimationState.start(this.tickCount);
        } else if (id == 5) {
            this.attackAnimationState.stop();
            this.attack2AnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    public boolean isPerformingCustomAttack() {
        return this.currentAttackType != 0;
    }

    public void startCustomAttack(int attackType) {
        if (this.level().isClientSide || this.isPerformingCustomAttack()) {
            return;
        }
        this.currentAttackType = attackType == 2 ? 2 : 1;
        this.currentAttackTick = 0;
        this.attackDidDamage = false;
        this.level().broadcastEntityEvent(this, this.currentAttackType == 1 ? (byte) 4 : (byte) 5);
    }

    private void tickCustomAttack() {
        if (!this.isPerformingCustomAttack()) {
            return;
        }

        this.currentAttackTick++;
        LivingEntity target = this.getTarget();

        if (!this.attackDidDamage && target != null && target.isAlive()) {
            int hitTick = this.currentAttackType == 1 ? ATTACK_1_HIT_TICK : ATTACK_2_HIT_TICK;
            if (this.currentAttackTick >= hitTick && this.isWithinAttackRange(target)) {
                float damage = this.currentAttackType == 1 ? ATTACK_1_DAMAGE : ATTACK_2_DAMAGE;
                if (target.hurt(this.damageSources().mobAttack(this), damage)) {
                    this.doEnchantDamageEffects(this, target);
                }
                this.attackDidDamage = true;
            }
        }

        int attackLength = this.currentAttackType == 1 ? ATTACK_1_LENGTH : ATTACK_2_LENGTH;
        if (this.currentAttackTick >= attackLength) {
            this.currentAttackType = 0;
            this.currentAttackTick = 0;
            this.attackDidDamage = false;
        }
    }

    private boolean isWithinAttackRange(LivingEntity target) {
        double attackReachSqr = (this.getBbWidth() * 2.0F) * (this.getBbWidth() * 2.0F) + target.getBbWidth();
        return this.distanceToSqr(target) <= attackReachSqr;
    }

    private static class SkyGuardianMeleeAttackGoal extends MeleeAttackGoal {
        private final SkyGuardianEntity guardian;

        public SkyGuardianMeleeAttackGoal(SkyGuardianEntity guardian, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(guardian, speedModifier, followingTargetEvenIfNotSeen);
            this.guardian = guardian;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target) {
            if (this.mob.distanceToSqr(target) <= this.mob.distanceToSqr(target) && this.getTicksUntilNextAttack() <= 0 && !this.guardian.isPerformingCustomAttack()) {
                this.resetAttackCooldown();
                int attackType = this.guardian.getRandom().nextFloat() < 0.65F ? 1 : 2;
                this.guardian.startCustomAttack(attackType);
            }
        }
    }

}

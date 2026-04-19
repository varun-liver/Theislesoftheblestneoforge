package com.isles.entity;

import com.isles.blest;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerPlayer;

public class TheGuardianEntity extends Monster {
    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);
    private static final float SMASH_DAMAGE = 5.0F;
    private static final int SMASH_HIT_TICK = 5;
    private static final int SMASH_LENGTH = 60;

    private static final float PUNCH_DAMAGE = 7.0F;
    private static final int PUNCH_HIT_TICK = 18;
    private static final int PUNCH_LENGTH = 45;

    private static final int SPIN_LENGTH = 40;
    private static final float SPIN_DAMAGE = 8.0F;
    private static final int SPIN_HIT_TICK = 5;

    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState attack2AnimationState = new AnimationState();
    public final AnimationState attack3AnimationState = new AnimationState();
    private int currentAttackType = 0;
    private int currentAttackTick = 0;
    private boolean attackDidDamage = false;

    public TheGuardianEntity(EntityType<? extends TheGuardianEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.45D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TheGuardianMeleeAttackGoal(this, 1.15D, true));
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
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attack2AnimationState.stop();
            this.attack3AnimationState.stop();
            this.attackAnimationState.start(this.tickCount);
        } else if (id == 5) {
            this.attackAnimationState.stop();
            this.attack3AnimationState.stop();
            this.attack2AnimationState.start(this.tickCount);
        } else if (id == 6) {
            this.attackAnimationState.stop();
            this.attack2AnimationState.stop();
            this.attack3AnimationState.start(this.tickCount);
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
        this.currentAttackType = attackType == 3 ? 3 : (attackType == 2 ? 2 : 1);
        this.currentAttackTick = 0;
        this.attackDidDamage = false;
        byte eventId = this.currentAttackType == 1 ? (byte) 4 : (this.currentAttackType == 2 ? (byte) 5 : (byte) 6);
        this.level().broadcastEntityEvent(this, eventId);
        if (this.currentAttackType == 3) {
            this.onSpinStart();
        }
    }

    private void tickCustomAttack() {
        if (!this.isPerformingCustomAttack()) {
            return;
        }

        this.currentAttackTick++;
        LivingEntity target = this.getTarget();

        if (!this.attackDidDamage && target != null && target.isAlive()) {
            int hitTick = this.currentAttackType == 1 ? SMASH_HIT_TICK
                    : (this.currentAttackType == 2 ? PUNCH_HIT_TICK : SPIN_HIT_TICK);
            if (this.currentAttackTick >= hitTick && this.isWithinAttackRange(target)) {
                float damage = this.currentAttackType == 1 ? SMASH_DAMAGE
                        : (this.currentAttackType == 2 ? PUNCH_DAMAGE : SPIN_DAMAGE);
                if (target.hurt(this.damageSources().mobAttack(this), damage)) {
                    this.doEnchantDamageEffects(this, target);
                }
                this.attackDidDamage = true;
            }
        }

        int attackLength = this.currentAttackType == 1 ? SMASH_LENGTH
                : (this.currentAttackType == 2 ? PUNCH_LENGTH : SPIN_LENGTH);
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

    protected void onSpinStart() {
        this.level().playSound(null,this.blockPosition(),blest.THE_GUARDIAN_WHOOSH.get(), SoundSource.HOSTILE,1.0F,1.0F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return blest.THE_GUARDIAN_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return blest.THE_GUARDIAN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return blest.THE_GUARDIAN_DEATH.get();
    }


    private static class TheGuardianMeleeAttackGoal extends MeleeAttackGoal {
        private final TheGuardianEntity theguardian;

        public TheGuardianMeleeAttackGoal(TheGuardianEntity theguardian, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(theguardian, speedModifier, followingTargetEvenIfNotSeen);
            this.theguardian = theguardian;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target) {
            if (this.mob.distanceToSqr(target) <= this.mob.distanceToSqr(target) && this.getTicksUntilNextAttack() <= 0 && !this.theguardian.isPerformingCustomAttack()) {
                this.resetAttackCooldown();
                float roll = this.theguardian.getRandom().nextFloat();
                int attackType = roll < 0.5F ? 1 : (roll < 0.85F ? 2 : 3);
                this.theguardian.startCustomAttack(attackType);
            }
        }
    }
}

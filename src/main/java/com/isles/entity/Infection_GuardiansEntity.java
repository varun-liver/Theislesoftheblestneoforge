package com.isles.entity;

import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EquipmentSlot;
import com.isles.blest;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;

public class Infection_GuardiansEntity extends Monster {
    private static final float PUNCH_DAMAGE = 20.0F;
    private static final float PUNCH_DAMAGE_SKY_CATALYST = 6.0F;
    private static final int PUNCH_HIT_TICK = 10;
    private static final int PUNCH_LENGTH = 40;

    public final AnimationState punchAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();

    private int punchTick = 0;
    private boolean punchDidDamage = false;
    private boolean isPunching = false;

    public Infection_GuardiansEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new Infection_GuardiansMeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            if (this.isMoving()) {
                this.walkAnimationState.startIfStopped(this.tickCount);
            } else {
                this.walkAnimationState.stop();
            }
        } else {
            this.tickPunchAttack();
        }
    }

    private boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.punchAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    public boolean isPerformingPunchAttack() {
        return this.isPunching;
    }

    public void startPunchAttack() {
        if (this.level().isClientSide || this.isPunching) {
            return;
        }
        this.isPunching = true;
        this.punchTick = 0;
        this.punchDidDamage = false;
        this.level().broadcastEntityEvent(this, (byte) 4);
    }

    private void tickPunchAttack() {
        if (!this.isPunching) {
            return;
        }

        this.punchTick++;
        LivingEntity target = this.getTarget();

        if (!this.punchDidDamage && target != null && target.isAlive()) {
            if (this.punchTick >= PUNCH_HIT_TICK && this.isWithinAttackRange(target)) {
                float damage = getPunchDamage(target);
                if (target.hurt(this.damageSources().mobAttack(this), damage)) {
                    this.doEnchantDamageEffects(this, target);
                }
                this.punchDidDamage = true;
            }
        }

        if (this.punchTick >= PUNCH_LENGTH) {
            this.isPunching = false;
            this.punchTick = 0;
            this.punchDidDamage = false;
        }
    }

    private boolean isWithinAttackRange(LivingEntity target) {
        double attackReachSqr = (this.getBbWidth() * 2.0F) * (this.getBbWidth() * 2.0F) + target.getBbWidth();
        return this.distanceToSqr(target) <= attackReachSqr;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return blest.Infection_Guardians_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return blest.Infection_Guardians_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return blest.Infection_Guardians_DEATH.get();
    }

    private static float getPunchDamage(LivingEntity target) {
        if (target instanceof Player player && isWearingSkyCatalyst(player)) {
            return PUNCH_DAMAGE_SKY_CATALYST;
        }
        return PUNCH_DAMAGE;
    }

    private static boolean isWearingSkyCatalyst(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(blest.SKY_CATALYST_HELMET.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(blest.SKY_CATALYST_CHESTPLATE.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(blest.SKY_CATALYST_LEGGINGS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(blest.SKY_CATALYST_BOOTS.get());
    }

    private static class Infection_GuardiansMeleeAttackGoal extends MeleeAttackGoal {
        private final Infection_GuardiansEntity guardian;

        private Infection_GuardiansMeleeAttackGoal(Infection_GuardiansEntity guardian, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(guardian, speedModifier, followingTargetEvenIfNotSeen);
            this.guardian = guardian;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target) {
            if (this.mob.distanceToSqr(target) <= this.mob.distanceToSqr(target)
                    && this.getTicksUntilNextAttack() <= 0
                    && !this.guardian.isPerformingPunchAttack()) {
                this.resetAttackCooldown();
                this.guardian.startPunchAttack();
            }
        }
    }
}

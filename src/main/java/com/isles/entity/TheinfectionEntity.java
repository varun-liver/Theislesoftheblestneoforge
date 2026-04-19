package com.isles.entity;

import com.isles.ArmorStandCutsceneManager;
import com.isles.Config;
import com.isles.InfectionBlockEntity;
import com.isles.blest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

public class TheinfectionEntity extends Monster {
    private static final float SMASH_DAMAGE = 30.0F;
    private static final int SMASH_HIT_TICK = 5;
    private static final int SMASH_LENGTH = 60;

    private static final float BLAST_DAMAGE = 7.0F;
    private static final int BLAST_HIT_TICK = 18;
    private static final int BLAST_LENGTH = 45;

    private static final int SUMMON_LENGTH = 40;

    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState attack2AnimationState = new AnimationState();
    public final AnimationState attack3AnimationState = new AnimationState();
    private int currentAttackType = 0;
    private int currentAttackTick = 0;
    private boolean attackDidDamage = false;

    public TheinfectionEntity(EntityType<? extends TheinfectionEntity> type, Level level) {
        super(type, level);
    }
    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.RED,
            BossEvent.BossBarOverlay.PROGRESS
    ).setDarkenScreen(true);
    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (!this.level().isClientSide) {
            Config.spreadInfection = false;
            InfectionBlockEntity.revertAll();
            if (damageSource.getEntity() instanceof ServerPlayer player) {
                ArmorStandCutsceneManager.TheInfectionCutscene(player);
            }
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
    private static boolean isWearingSkyCatalyst(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(blest.SKY_CATALYST_HELMET.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(blest.SKY_CATALYST_CHESTPLATE.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(blest.SKY_CATALYST_LEGGINGS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(blest.SKY_CATALYST_BOOTS.get());
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TheInfectionMeleeAttackGoal(this, 1.15D, true));
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
            this.bossEvent.setProgress(this.getHealth()/this.getMaxHealth());
        }
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
            this.onSummonStart(this.level(),this.blockPosition());
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
            this.onSummonStart(this.level(),this.blockPosition());
        }
    }

    private void tickCustomAttack() {
        if (!this.isPerformingCustomAttack()) {
            return;
        }

        this.currentAttackTick++;
        LivingEntity target = this.getTarget();

        if (!this.attackDidDamage && target != null && target.isAlive()) {
            int hitTick = this.currentAttackType == 1 ? SMASH_HIT_TICK : BLAST_HIT_TICK;
            if (this.currentAttackTick >= hitTick && this.isWithinAttackRange(target)) {
                float damage = this.currentAttackType == 1 ? SMASH_DAMAGE : BLAST_DAMAGE;
                
                // Reduce damage if the target is a player wearing full sky_catalyst armor
                if (target instanceof Player player && isWearingSkyCatalyst(player)) {
                    damage = this.currentAttackType == 1 ? 7.0F : 2.0F;
                }

                if (this.currentAttackType != 3) {
                    if (target.hurt(this.damageSources().mobAttack(this), damage)) {
                        this.doEnchantDamageEffects(this, target);
                    }
                }
                this.attackDidDamage = true;
            }
        }

        int attackLength = this.currentAttackType == 1 ? SMASH_LENGTH
                : (this.currentAttackType == 2 ? BLAST_LENGTH : SUMMON_LENGTH);
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

    protected void onSummonStart(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            for(int i = 0; i < 10; i++) {
                Entity entity = blest.sky_guardian.get().create(level);
                if (entity != null) {
                    entity.moveTo(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
                    level.addFreshEntity(entity);
                }
                entity = EntityType.LIGHTNING_BOLT.create(level);
                if (entity != null) {
                    entity.moveTo(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
                    level.addFreshEntity(entity);
                }
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            blest.glowing_stars.get(),
                            pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                            8,
                            0.35, 0.35, 0.35,
                            0.01
                    );
                }
            }
        }
    }

    private static class TheInfectionMeleeAttackGoal extends MeleeAttackGoal {
        private final TheinfectionEntity theinfection;

        public  TheInfectionMeleeAttackGoal(TheinfectionEntity theinfection, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(theinfection, speedModifier, followingTargetEvenIfNotSeen);
            this.theinfection = theinfection;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target) {
            if (this.mob.distanceToSqr(target) <= this.mob.distanceToSqr(target) && this.getTicksUntilNextAttack() <= 0 && !this.theinfection.isPerformingCustomAttack()) {
                this.resetAttackCooldown();
                float roll = this.theinfection.getRandom().nextFloat();
                int attackType = roll < 0.5F ? 1 : (roll < 0.85F ? 2 : 3);
                this.theinfection.startCustomAttack(attackType);
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return blest.THE_INFECTION_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return blest.THE_INFECTION_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return blest.THE_INFECTION_DEATH.get();
    }
}

package com.isles;

import com.isles.client.AnimationLoader;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Rotations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import org.slf4j.Logger;
import com.isles.network.CutsceneEndS2CPacket;
import com.isles.network.CutsceneStartS2CPacket;
import com.isles.network.ModNetwork;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;     // Fixes SubscribeEvent error
import net.neoforged.bus.api.EventPriority;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = blest.MODID)
public class ArmorStandCutsceneManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<AnimationInstance> ACTIVE_ANIMATIONS = new ArrayList<>();
    private static final Set<UUID> END_REQUESTS = ConcurrentHashMap.newKeySet();

    public static void requestEnd(ServerPlayer player) {
        if (player == null) return;
        END_REQUESTS.add(player.getUUID());
    }

    private static final String MODEL_ANIM_RESOURCE = "assets/" + blest.MODID + "/animations/modelanimation.json";
    private static final String MODEL_ANIM_NAME = "animation";
    private static volatile AnimationLoader.BedrockAnimation MODEL_ANIM;

    // Overlay script (client). Keep this ASCII-only.
    private static final String CARD1_TITLE = "The Infection";
    private static final String CARD1_SUBTITLE = "It ends...";
    private static final String CARD2_TITLE = "Somewhere Above";
    private static final String CARD2_SUBTITLE = "A forgotten promise stirs.";
    private static final String CARD3_TITLE = "It Is finished";
    private static final String CARD3_SUBTITLE = "After 5 eons";
    private static final String CARD4_TITLE = "Or Is it...";
    private static final String CARD4_SUBTITLE = "";
    private static final String SCROLL_TEXT = String.join("\n",
        "The Isles Of The Blest",
            "\u00A72Where am I?\n",
            "\u00A71It might be too late\n",
            "\u00A72What do you mean\n",
            "\u00A71Your Success only made them stronger\n",
            "\u00A71There was a age before you,\n",
            "\u00A71Before everything\n",
            "\u00A71You must defeat them\n",
            "\u00A72Defeat who?\n",
            "\u00A72Who Are You?\n",
            "\u00A71The Real question is, who are you {player}"
    );

    private static final int CARD1_TICKS = 50; // 2.5s
    private static final int GAP1_TICKS = 10;  // 0.5s
    private static final int CARD2_TICKS = 50; // 2.5s
    private static final int GAP2_TICKS = 10;  // 0.5s
    private static final int CARD3_TICKS = 50; // 2.5s
    private static final int GAP3_TICKS = 10;
    private static final int CARD4_TICKS = 50; // 2.5s
    private static final int GAP4_TICKS = 10;
    // Minimum scroll time; actual scroll duration is estimated from content length so it doesn't cut off early.
    private static final int SCROLL_TICKS = 240; // 12s minimum

    private static int estimateScrollTicks(String text) {
        if (text == null || text.isEmpty()) return SCROLL_TICKS;
        int lines = text.split("\\R", -1).length;
        // Rough heuristic: longer text needs more time. This is deliberately conservative.
        int ticks = 160 + (lines * 35) + (text.length() / 4);
        int min = SCROLL_TICKS;
        int max = 20 * 60 * 20; // 20 minutes hard cap
        if (ticks < min) ticks = min;
        if (ticks > max) ticks = max;
        return ticks;
    }

    private static AnimationLoader.BedrockAnimation getModelAnimation() {
        AnimationLoader.BedrockAnimation anim = MODEL_ANIM;
        if (anim != null) return anim;
        synchronized (ArmorStandCutsceneManager.class) {
            if (MODEL_ANIM != null) return MODEL_ANIM;
            try {
                MODEL_ANIM = AnimationLoader.loadBedrockAnimation(MODEL_ANIM_RESOURCE, MODEL_ANIM_NAME);
                LOGGER.info("Loaded cutscene animation {}#{}", MODEL_ANIM_RESOURCE, MODEL_ANIM_NAME);
                return MODEL_ANIM;
            } catch (RuntimeException e) {
                LOGGER.error("Failed to load cutscene animation {}#{}", MODEL_ANIM_RESOURCE, MODEL_ANIM_NAME, e);
                return null;
            }
        }
    }

    public static void TheInfectionCutscene(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        // Copy the player's equipped armor onto the stand, and strip it from the player so it doesn't render
        // while they're invisible (invisibility does not hide worn armor in vanilla rendering).
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD).copy();
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST).copy();
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS).copy();
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET).copy();

        ArmorStand stand = new ArmorStand(level, x, y, z);
        // Keep the stand oriented the same way the player was facing when the cutscene started.
        stand.setYRot(player.getYRot());
        stand.setYHeadRot(player.getYHeadRot());
        stand.setNoGravity(true);
        stand.setInvulnerable(true);
        stand.setInvisible(false);
        stand.setNoBasePlate(true);
        stand.setShowArms(true);

        stand.setItemSlot(EquipmentSlot.HEAD, head);
        stand.setItemSlot(EquipmentSlot.CHEST, chest);
        stand.setItemSlot(EquipmentSlot.LEGS, legs);
        stand.setItemSlot(EquipmentSlot.FEET, feet);
        stand.setItemSlot(EquipmentSlot.MAINHAND, player.getItemBySlot(EquipmentSlot.MAINHAND));

        level.addFreshEntity(stand);

        // Strip the player's armor so it doesn't render while invisible.
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
        player.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
        player.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);

        // Camera anchor: smoother than teleporting the player every tick.
        ArmorStand cameraStand = new ArmorStand(level, x, y, z);
        cameraStand.setYRot(player.getYRot());
        cameraStand.setYHeadRot(player.getYHeadRot());
        cameraStand.setNoGravity(true);
        cameraStand.setInvulnerable(true);
        cameraStand.setInvisible(true);
        cameraStand.setNoBasePlate(true);
        level.addFreshEntity(cameraStand);

        player.setInvisible(true);
        player.setDeltaMovement(0, 0, 0);
        player.setCamera(cameraStand);

        // Start animation
        AnimationLoader.BedrockAnimation anim = getModelAnimation();
        float lengthSeconds = (anim == null || anim.lengthSeconds <= 0f) ? 4.0f : anim.lengthSeconds;
        int animTicks = Math.max(1, Math.round(lengthSeconds * 20.0f));

        String resolvedScrollText = SCROLL_TEXT.replace("{player}", player.getName().getString());
        int scrollTicks = estimateScrollTicks(resolvedScrollText);

        int overlayTicks = CARD1_TICKS + GAP1_TICKS + CARD2_TICKS + GAP2_TICKS + CARD3_TICKS + GAP3_TICKS + CARD4_TICKS + GAP4_TICKS + scrollTicks;
        int totalTicks = Math.max(animTicks, overlayTicks);

        ModNetwork.sendToPlayer(player, new CutsceneStartS2CPacket(
            totalTicks,
            CARD1_TICKS,
            GAP1_TICKS,
            CARD2_TICKS,
            GAP2_TICKS,
            CARD3_TICKS,
            GAP3_TICKS,
            CARD4_TICKS,
            GAP4_TICKS,
            scrollTicks,
            CARD1_TITLE,
            CARD1_SUBTITLE,
            CARD2_TITLE,
            CARD2_SUBTITLE,
            CARD3_TITLE,
            CARD3_SUBTITLE,
            CARD4_TITLE,
            CARD4_SUBTITLE,
            resolvedScrollText
        ));
        ACTIVE_ANIMATIONS.add(new AnimationInstance(stand, cameraStand, player, x, y, z, anim, stand.getYRot(), animTicks, totalTicks, head, chest, legs, feet));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        Iterator<AnimationInstance> iterator = ACTIVE_ANIMATIONS.iterator();
        while (iterator.hasNext()) {
            AnimationInstance anim = iterator.next();
            if (anim.player != null && END_REQUESTS.remove(anim.player.getUUID())) {
                iterator.remove();
                anim.finish();
                continue;
            }
            if (anim.tick()) {
                iterator.remove();
                anim.finish();
            }
        }
    }


    private static class AnimationInstance {
        private final ArmorStand stand;
        private final ArmorStand cameraStand;
        private final ServerPlayer player;
        private final double startX, startY, startZ;
        private final AnimationLoader.BedrockAnimation anim;
        private final int maxTicks;
        private final int animTicks;
        private final float baseYaw;
        private final ItemStack headArmor;
        private final ItemStack chestArmor;
        private final ItemStack legsArmor;
        private final ItemStack feetArmor;
        private boolean animationStandDiscarded = false;
        private int currentTick = 0;

        public AnimationInstance(
            ArmorStand stand,
            ArmorStand cameraStand,
            ServerPlayer player,
            double x,
            double y,
            double z,
            AnimationLoader.BedrockAnimation anim,
            float baseYaw,
            int animTicks,
            int maxTicks,
            ItemStack headArmor,
            ItemStack chestArmor,
            ItemStack legsArmor,
            ItemStack feetArmor
        ) {
            this.stand = stand;
            this.cameraStand = cameraStand;
            this.player = player;
            this.startX = x;
            this.startY = y;
            this.startZ = z;
            this.anim = anim;
            this.baseYaw = baseYaw;
            this.animTicks = Math.max(1, animTicks);
            this.headArmor = headArmor == null ? ItemStack.EMPTY : headArmor;
            this.chestArmor = chestArmor == null ? ItemStack.EMPTY : chestArmor;
            this.legsArmor = legsArmor == null ? ItemStack.EMPTY : legsArmor;
            this.feetArmor = feetArmor == null ? ItemStack.EMPTY : feetArmor;
            this.maxTicks = Math.max(1, maxTicks);
        }

        public boolean tick() {
            if (currentTick >= maxTicks) return true;

            // Despawn the animated stand once the animation has finished.
            if (!animationStandDiscarded && currentTick >= animTicks) {
                animationStandDiscarded = true;
                stand.discard();
            }

            float time = currentTick / 20.0f;

            // Drive the stand directly from modelanimation.json (Blockbench / Bedrock keyframes).
            String groupBone = pickBone(anim, "group", "body", "bone", "root");
            float[] groupRot = (anim == null) ? new float[]{0f, 0f, 0f} : anim.sampleRotation(groupBone, time);
            float[] groupPos = (anim == null) ? new float[]{0f, 0f, 0f} : anim.samplePosition(groupBone, time);
            if (!animationStandDiscarded && stand.isAlive()) {
                stand.setBodyPose(new Rotations(groupRot[0], groupRot[1], groupRot[2]));
            }
            
            double newX = startX + groupPos[0] / 16.0;
            double newY = startY + groupPos[1] / 16.0;
            double newZ = startZ + groupPos[2] / 16.0;
            if (!animationStandDiscarded && stand.isAlive()) {
                stand.setPos(newX, newY, newZ);
            }

            // Move camera anchor (smoother than teleporting the player each tick).
            if (player != null) {
                player.setInvisible(true);
                player.setDeltaMovement(0, 0, 0);

                if (cameraStand != null && cameraStand.isAlive()) {
                    double forward = 0.8; // blocks
                    double yawRad = Math.toRadians(baseYaw);
                    double fx = -Math.sin(yawRad);
                    double fz = Math.cos(yawRad);
                    cameraStand.setPos(newX + fx * forward, newY, newZ + fz * forward);
                    cameraStand.setYRot(baseYaw);
                    cameraStand.setYHeadRot(baseYaw);
                } else {
                    // If something killed the camera stand, at least ensure the camera is reset.
                    player.setCamera((Entity) player);
                }
            }

            // Leg2 (Right Leg)
            String rightLegBone = pickBone(anim, "leg2", "right_leg", "rightleg", "leg_r", "rleg");
            float[] leg2Rot = (anim == null) ? new float[]{0f, 0f, 0f} : anim.sampleRotation(rightLegBone, time);
            float[] rightLegFinal = addRot(groupRot, leg2Rot);
            if (!animationStandDiscarded && stand.isAlive()) {
                stand.setRightLegPose(new Rotations(rightLegFinal[0], rightLegFinal[1], rightLegFinal[2]));
            }

            // Leg1 (Left Leg)
            String leftLegBone = pickBone(anim, "leg1", "left_leg", "leftleg", "leg_l", "lleg");
            float[] leg1Rot = (anim == null) ? new float[]{0f, 0f, 0f} : anim.sampleRotation(leftLegBone, time);
            float[] leftLegFinal = addRot(groupRot, leg1Rot);
            if (!animationStandDiscarded && stand.isAlive()) {
                stand.setLeftLegPose(new Rotations(leftLegFinal[0], leftLegFinal[1], leftLegFinal[2]));
            }

            // Arm2 (Right Arm)
            String rightArmBone = pickBone(anim, "arm2", "right_arm", "rightarm", "arm_r", "rarm");
            float[] arm2Rot = (anim == null) ? new float[]{0f, 0f, 0f} : anim.sampleRotation(rightArmBone, time);
            float[] rightArmFinal = addRot(groupRot, arm2Rot);
            if (!animationStandDiscarded && stand.isAlive()) {
                stand.setRightArmPose(new Rotations(rightArmFinal[0], rightArmFinal[1], rightArmFinal[2]));
            }

            // Arm1 (Left Arm)
            String leftArmBone = pickBone(anim, "arm1", "left_arm", "leftarm", "arm_l", "larm");
            float[] arm1Rot = (anim == null) ? new float[]{0f, 0f, 0f} : anim.sampleRotation(leftArmBone, time);
            float[] leftArmFinal = addRot(groupRot, arm1Rot);
            if (!animationStandDiscarded && stand.isAlive()) {
                stand.setLeftArmPose(new Rotations(leftArmFinal[0], leftArmFinal[1], leftArmFinal[2]));
            }

            // Head
            String headBone = pickBone(anim, "head", "Head");
            float[] headRot = (anim == null) ? new float[]{0f, 0f, 0f} : anim.sampleRotation(headBone, time);
            float[] headFinal = addRot(groupRot, headRot);
            if (!animationStandDiscarded && stand.isAlive()) {
                stand.setHeadPose(new Rotations(headFinal[0], headFinal[1], headFinal[2]));
            }

            currentTick++;
            return false;
        }

        public void finish() {
            stand.discard();
            if (cameraStand != null) {
                cameraStand.discard();
            }
            if (player != null) {
                player.setCamera((Entity) player);
                player.setInvisible(false);
                // Restore the player's armor after the cutscene.
                player.setItemSlot(EquipmentSlot.HEAD, headArmor);
                player.setItemSlot(EquipmentSlot.CHEST, chestArmor);
                player.setItemSlot(EquipmentSlot.LEGS, legsArmor);
                player.setItemSlot(EquipmentSlot.FEET, feetArmor);
                ModNetwork.sendToPlayer(player, new CutsceneEndS2CPacket());
            }
        }

        private static String pickBone(AnimationLoader.BedrockAnimation anim, String... candidates) {
            if (candidates == null || candidates.length == 0) return "";
            if (anim == null) return candidates[0];
            for (String c : candidates) {
                if (c != null && !c.isEmpty() && anim.hasBone(c)) return c;
            }
            return candidates[0];
        }

        private static float[] addRot(float[] a, float[] b) {
            float ax = (a != null && a.length > 0) ? a[0] : 0f;
            float ay = (a != null && a.length > 1) ? a[1] : 0f;
            float az = (a != null && a.length > 2) ? a[2] : 0f;
            float bx = (b != null && b.length > 0) ? b[0] : 0f;
            float by = (b != null && b.length > 1) ? b[1] : 0f;
            float bz = (b != null && b.length > 2) ? b[2] : 0f;
            return new float[]{ax + bx, ay + by, az + bz};
        }
    }
}

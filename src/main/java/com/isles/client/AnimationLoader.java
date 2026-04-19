package com.isles.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Lightweight loader for Blockbench / Bedrock-style animation JSON.
 *
 * This is intentionally "common-safe" (no client-only classes) so it can be used to
 * drive server-side ArmorStand pose animations in cutscenes.
 */
public final class AnimationLoader {
    private AnimationLoader() {}

    private static String normalizeBoneName(String name) {
        if (name == null) return "";
        return name.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Loads a Bedrock animation from a classpath resource.
     *
     * @param classpathResource e.g. {@code "assets/theislesoftheblest/animations/modelanimation.json"}
     * @param animationName     key inside {@code animations.{name}} (e.g. {@code "animation"})
     */
    public static BedrockAnimation loadBedrockAnimation(String classpathResource, String animationName) {
        Objects.requireNonNull(classpathResource, "classpathResource");
        Objects.requireNonNull(animationName, "animationName");

        InputStream is = AnimationLoader.class.getClassLoader().getResourceAsStream(classpathResource);
        if (is == null) {
            throw new IllegalStateException("Missing animation resource on classpath: " + classpathResource);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            JsonObject root = JsonParser.parseReader(br).getAsJsonObject();
            JsonObject animations = root.getAsJsonObject("animations");
            if (animations == null) {
                throw new IllegalStateException("Invalid animation json (missing 'animations'): " + classpathResource);
            }

            JsonObject animObj = animations.getAsJsonObject(animationName);
            if (animObj == null) {
                throw new IllegalStateException("Missing animation '" + animationName + "' in: " + classpathResource);
            }

            float lengthSeconds = getAsFloat(animObj, "animation_length", 0f);
            JsonObject bonesObj = animObj.getAsJsonObject("bones");
            Map<String, BoneAnimation> bones = new HashMap<>();

            if (bonesObj != null) {
                for (Map.Entry<String, JsonElement> boneEntry : bonesObj.entrySet()) {
                    if (!boneEntry.getValue().isJsonObject()) continue;
                    String boneName = normalizeBoneName(boneEntry.getKey());
                    JsonObject boneObj = boneEntry.getValue().getAsJsonObject();

                    KeyframeChannel rotation = parseKeyframeChannel(boneObj.get("rotation"));
                    KeyframeChannel position = parseKeyframeChannel(boneObj.get("position"));
                    bones.put(boneName, new BoneAnimation(rotation, position));
                }
            }

            return new BedrockAnimation(lengthSeconds, bones);
        } catch (IOException e) {
            throw new IllegalStateException("Failed reading animation resource: " + classpathResource, e);
        }
    }

    private static float getAsFloat(JsonObject obj, String key, float defaultValue) {
        if (obj == null) return defaultValue;
        JsonElement el = obj.get(key);
        if (el == null || el.isJsonNull()) return defaultValue;
        try {
            return el.getAsFloat();
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    private static KeyframeChannel parseKeyframeChannel(JsonElement channelEl) {
        if (channelEl == null || channelEl.isJsonNull()) {
            return KeyframeChannel.EMPTY;
        }
        if (!channelEl.isJsonObject()) {
            return KeyframeChannel.EMPTY;
        }

        JsonObject channelObj = channelEl.getAsJsonObject();
        List<Keyframe> keys = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : channelObj.entrySet()) {
            float time;
            try {
                // Bedrock JSON uses string keys like "0.0"
                time = Float.parseFloat(entry.getKey().toLowerCase(Locale.ROOT));
            } catch (NumberFormatException ignored) {
                continue;
            }

            if (!entry.getValue().isJsonArray()) continue;
            JsonArray arr = entry.getValue().getAsJsonArray();
            if (arr.size() < 3) continue;
            float x = arr.get(0).getAsFloat();
            float y = arr.get(1).getAsFloat();
            float z = arr.get(2).getAsFloat();
            keys.add(new Keyframe(time, x, y, z));
        }

        if (keys.isEmpty()) return KeyframeChannel.EMPTY;
        keys.sort(Comparator.comparingDouble(k -> k.time));
        return new KeyframeChannel(keys);
    }

    public static final class BedrockAnimation {
        public final float lengthSeconds;
        private final Map<String, BoneAnimation> bones;

        private BedrockAnimation(float lengthSeconds, Map<String, BoneAnimation> bones) {
            this.lengthSeconds = lengthSeconds;
            this.bones = bones;
        }

        public boolean hasBone(String boneName) {
            return bones.containsKey(normalizeBoneName(boneName));
        }

        public float[] sampleRotation(String boneName, float timeSeconds) {
            BoneAnimation bone = bones.get(normalizeBoneName(boneName));
            if (bone == null) return new float[]{0f, 0f, 0f};
            return bone.rotation.sample(timeSeconds);
        }

        public float[] samplePosition(String boneName, float timeSeconds) {
            BoneAnimation bone = bones.get(normalizeBoneName(boneName));
            if (bone == null) return new float[]{0f, 0f, 0f};
            return bone.position.sample(timeSeconds);
        }
    }

    public static final class BoneAnimation {
        private final KeyframeChannel rotation;
        private final KeyframeChannel position;

        private BoneAnimation(KeyframeChannel rotation, KeyframeChannel position) {
            this.rotation = rotation == null ? KeyframeChannel.EMPTY : rotation;
            this.position = position == null ? KeyframeChannel.EMPTY : position;
        }
    }

    private static final class Keyframe {
        final float time;
        final float x;
        final float y;
        final float z;

        Keyframe(float time, float x, float y, float z) {
            this.time = time;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static final class KeyframeChannel {
        static final KeyframeChannel EMPTY = new KeyframeChannel(List.of());

        private final float[] times;
        private final float[][] values;

        private KeyframeChannel(List<Keyframe> keys) {
            this.times = new float[keys.size()];
            this.values = new float[keys.size()][3];
            for (int i = 0; i < keys.size(); i++) {
                Keyframe k = keys.get(i);
                times[i] = k.time;
                values[i][0] = k.x;
                values[i][1] = k.y;
                values[i][2] = k.z;
            }
        }

        float[] sample(float t) {
            if (times.length == 0) return new float[]{0f, 0f, 0f};
            if (t <= times[0]) return values[0].clone();
            int last = times.length - 1;
            if (t >= times[last]) return values[last].clone();

            // Linear scan is fine for tiny keyframe counts.
            for (int i = 0; i < last; i++) {
                float t1 = times[i];
                float t2 = times[i + 1];
                if (t >= t1 && t <= t2) {
                    float alpha = (t - t1) / (t2 - t1);
                    float[] v1 = values[i];
                    float[] v2 = values[i + 1];
                    return new float[]{
                        v1[0] + (v2[0] - v1[0]) * alpha,
                        v1[1] + (v2[1] - v1[1]) * alpha,
                        v1[2] + (v2[2] - v1[2]) * alpha
                    };
                }
            }
            return values[last].clone();
        }
    }
}

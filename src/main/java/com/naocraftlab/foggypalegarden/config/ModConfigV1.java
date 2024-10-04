package com.naocraftlab.foggypalegarden.config;

import java.util.Set;

public class ModConfigV1 extends ModConfig {

    // enable fog in biomes
    private final Set<String> biomes;
    // fog preset
    private final ForPreset fogPreset;
    // custom fog settings
    private final FogSettings customFog;

    public ModConfigV1(int version, Set<String> biomes, ForPreset fogPreset, FogSettings customFog) {
        super(version);
        this.biomes = biomes;
        this.fogPreset = fogPreset;
        this.customFog = customFog;
    }

    public Set<String> getBiomes() {
        return biomes;
    }

    public ForPreset getFogPreset() {
        return fogPreset;
    }

    public FogSettings getCustomFog() {
        return customFog;
    }

    public enum ForPreset {
        // preset by game difficulty
        DIFFICULTY_BASED,
        // low
        AMBIANCE,
        // medium
        I_AM_NOT_AFRAID_BUT,
        // hard
        STEPHEN_KING,
        // use custom fog settings
        CUSTOM
    }

    public static class FogSettings {

        // blocks
        private final float startDistance;
        // 0 - 15 points
        private final int skyLightStartLevel;
        // blocks
        private final float endDistance;
        // in blocks
        private final float surfaceHeightEnd;
        // persent
        private final float opacity;
        // percent/sec
        private final float encapsulationSpeed;

        public FogSettings(
                float startDistance,
                int skyLightStartLevel,
                float endDistance,
                float surfaceHeightEnd,
                float opacity,
                float encapsulationSpeed
        ) {
            this.startDistance = startDistance;
            this.skyLightStartLevel = skyLightStartLevel;
            this.endDistance = endDistance;
            this.surfaceHeightEnd = surfaceHeightEnd;
            this.opacity = opacity;
            this.encapsulationSpeed = encapsulationSpeed;
        }

        public float getStartDistance() {
            return startDistance;
        }

        public int getSkyLightStartLevel() {
            return skyLightStartLevel;
        }

        public float getEndDistance() {
            return endDistance;
        }

        public float getSurfaceHeightEnd() {
            return surfaceHeightEnd;
        }

        public float getOpacity() {
            return opacity;
        }

        public float getEncapsulationSpeed() {
            return encapsulationSpeed;
        }
    }
}

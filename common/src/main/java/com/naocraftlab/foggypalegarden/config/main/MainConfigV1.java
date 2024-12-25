package com.naocraftlab.foggypalegarden.config.main;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class MainConfigV1 extends MainConfig {

    /**
     * List of biomes for activating fog.
     */
    private final Set<String> biomes;

    /**
     * Fog preset.
     */
    private final FogPreset fogPreset;

    /**
     * Custom fog settings.
     */
    private final FogSettings customFog;

    public MainConfigV1(Set<String> biomes, FogPreset fogPreset, FogSettings customFog) {
        super(1);
        this.biomes = biomes;
        this.fogPreset = fogPreset;
        this.customFog = customFog;
    }

    public enum FogPreset {

        /**
         * Preset by game difficulty.
         */
        DIFFICULTY_BASED,

        /**
         * Low.
         */
        AMBIANCE,

        /**
         * Medium.
         */
        I_AM_NOT_AFRAID_BUT,

        /**
         * Hard.
         */
        STEPHEN_KING,

        /**
         * Use custom fog settings.
         */
        CUSTOM
    }

    @Data
    public static class FogSettings {
        /** fog start distance (in blocks) */
        private final float startDistance;
        /** level of light from sky to start fog (0 - 15) */
        private final int skyLightStartLevel;
        /** fog end distance (in blocks) */
        private final float endDistance;
        /** height above the surface to turn off fog (in blocks) */
        private final float surfaceHeightEnd;
        /** fog opacity (0.0 - 1.0) */
        private final float opacity;
        /** fog encapsulation speed (percent/sec) */
        private final float encapsulationSpeed;
    }
}

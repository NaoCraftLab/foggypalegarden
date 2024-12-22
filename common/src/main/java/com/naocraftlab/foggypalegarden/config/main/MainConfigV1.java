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

    /**
     * @param startDistance         fog start distance (in blocks)
     * @param skyLightStartLevel    level of light from sky to start fog (0 - 15)
     * @param endDistance           fog end distance (in blocks)
     * @param surfaceHeightEnd      height above the surface to turn off fog (in blocks)
     * @param opacity               fog opacity (0.0 - 1.0)
     * @param encapsulationSpeed    fog encapsulation speed (percent/sec)
     */
    public record FogSettings(
        float startDistance,
        int skyLightStartLevel,
        float endDistance,
        float surfaceHeightEnd,
        float opacity,
        float encapsulationSpeed
    ) {}
}

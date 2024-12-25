package com.naocraftlab.foggypalegarden.domain.model;

import lombok.Builder;
import lombok.Data;
import net.minecraft.world.Difficulty;

@Data
@Builder
public class Environment {
    private final String dimension;
    private final String biome;
    private final Float biomeTemperature;
    private final Difficulty difficulty;
    private final Weather weather;
    private final long timeOfDay;
    private final int skyLightLevel;
    private final double height;
    private final double heightAboveSurface;
    private final Color gameFogColor;
    private final float fogDensity;
}
